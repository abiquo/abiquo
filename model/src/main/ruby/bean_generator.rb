if ARGV.size < 2
  puts "Usage: ruby #{$0} package.class_name table_name property:type property:type ..."
  exit 1
end

require 'erb'
require 'fileutils'
include FileUtils

def underscore(camel_cased_word)
  camel_cased_word.to_s.gsub(/::/, '/').
    gsub(/([A-Z]+)([A-Z][a-z])/,'\1_\2').
    gsub(/([a-z\d])([A-Z])/,'\1_\2').
    tr("-", "_").
    downcase
end

def camelcase(lower_case_and_underscored_word, first_letter_in_uppercase = true)
  if first_letter_in_uppercase
    lower_case_and_underscored_word.to_s.gsub(/(?:^|_)(.)/) { $1.upcase }
  else
    lower_case_and_underscored_word[0...1].downcase + camelcase(lower_case_and_underscored_word)[1..-1]
  end
end

class String
  def /(arg)
    File.join self, arg
  end
end

def confirm?(file_path)
  if File.exist? file_path
    print "#{file_path} already exists. Do you want to override it? (Y/n) "
    reply = (STDIN.getc.chr).chop
    reply == '' || reply.downcase == 'y'
  end || true
end

MAIN_JAVA = File.expand_path('../java', File.dirname(__FILE__))
TEST_JAVA = File.expand_path('../../test/java', File.dirname(__FILE__))
TEMPLATES = File.expand_path('templates', File.dirname(__FILE__))

@package = ARGV[0].split('.')[0..-2].join('.')
@class = ARGV[0].split('.')[-1]
@table = ARGV[1]

@properties = Hash[ARGV[2..-1].map {|p| [ p.split(':')[0], p.split(':')[1..-1] ] }]

package_path = @package.gsub('.', '/')
class_file_name = "#{@class}.java"

cd(MAIN_JAVA) { mkdir_p package_path }
cd(TEST_JAVA) { mkdir_p package_path }

@class_camelcase = camelcase(underscore(@class), false)

@column_id = "#{@class_camelcase}Id"
id_template = ERB.new(File.read(TEMPLATES / 'property_id.erb')).result

property_templates = []
@assoc_generators = []
@property_equals = []
@dto_fields = []
@unique_fields = []
@unique_properties = []

def to_dto(property, type, camelcase_first_upcase)
%Q{private #{type} #{property};

public #{type} get#{camelcase_first_upcase}()
{
    return #{property};
}

public void set#{camelcase_first_upcase}(#{type} #{property})
{
    this.#{property} = #{property};
}
}
end

def unique(property, upcase, clazz, camelcase_first_upcase)
%Q{private static Criterion equal#{camelcase_first_upcase}(String #{property})
{
    return Restrictions.eq(#{clazz}.#{upcase}_PROPERTY, #{property});
}

public boolean existsAnyWith#{camelcase_first_upcase}(String #{property})
{
    return this.existsAnyByCriterions(equal#{camelcase_first_upcase}(#{property}));
}

public boolean existsAnyOtherWith#{camelcase_first_upcase}(#{clazz} entity, String #{property})
{
    return this.existsAnyOtherByCriterions(entity, equal#{camelcase_first_upcase}(#{property}));
}
}
end

@properties.each do |property, type|
  template = case type[0]
             when 'assoc'
               type.length > 2 ? "association_#{type[1]}.erb" : 'association_N1.erb'
             when 'enum'
               'property_enum.erb'
             else
             	"property_#{type[0]}.erb"
             end

  @upcase = underscore(property).upcase
  @property = camelcase(underscore(property), false)
  @camelcase_first_upcase = camelcase(property)

  @type = case type[0]
          when 'string'
            camelcase(type[0])
          when 'enum'
            camelcase(type[1])
          when 'assoc'
            type.length > 2 ? type[2] : type[1]
          else
            type[0]
          end

  property_templates << ERB.new(File.read(TEMPLATES / template)).result
  
  if type[0] == 'string' and type.length > 1
    @unique_fields << unique(@property, @upcase, @class, @camelcase_first_upcase)
    @unique_properties << @camelcase_first_upcase
  end

  @assoc_generators << type[1] if type[0] == 'assoc'
  @property_equals << "#{@class}.#{@upcase}_PROPERTY" unless type[0] == 'assoc'

  @dto_fields << to_dto(@property, @type, @camelcase_first_upcase) unless type[0] == 'assoc'
end

templates = property_templates.join("\n")

@bean_path = MAIN_JAVA / package_path / class_file_name

if confirm?(@bean_path)
  File.open(@bean_path, 'w') do |file|
    file.write <<-EOH
  package #{@package};

  import java.util.List;
  import java.util.Set;

  import javax.persistence.Entity;
  import javax.persistence.Table;

  import com.abiquo.server.core.common.DefaultEntityBase;
  import com.softwarementors.validation.constraints.Required;

  @Entity
  @Table(name = #{@class}.TABLE_NAME)
  @org.hibernate.annotations.Table(appliesTo = #{@class}.TABLE_NAME)
  public class #{@class} extends DefaultEntityBase
  {
      public static final String TABLE_NAME = "#{@table}";

      // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
      // code
      protected #{@class}()
      {
          // Just for JPA support
      }

      #{id_template}

      #{templates}
  }
    EOH
  end
end

@dto_path = MAIN_JAVA / package_path / "#{@class}Dto.java"

if confirm?(@dto_path)
  File.open(@dto_path, 'w') do |file|
    file.write <<-EOH
  package #{@package};

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class #{@class}Dto extends SingleResourceTransportDto
  {
      private Integer id;
      public Integer getId()
      {
          return id;
      }

      public void setId(Integer id)
      {
          this.id = id;
      }

      #{@dto_fields.join("\n")}
  }
    EOH
  end
end

@dao_path = MAIN_JAVA / package_path / "#{@class}DAO.java"

if confirm?(@dao_path)
  File.open(@dao_path, 'w') do |file|
    file.write <<-EOH
  package #{@package};

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpa#{@class}DAO")
  public class #{@class}DAO extends DefaultDAOBase<Integer, #{@class}>
  {
      public #{@class}DAO()
      {
          super(#{@class}.class);
      }

      public #{@class}DAO(EntityManager entityManager)
      {
          super(#{@class}.class, entityManager);
      }

      #{@unique_fields.join("\n")}
  }
    EOH
  end
end

@generator_path = TEST_JAVA / package_path / "#{@class}Generator.java"

if confirm?(@generator_path)
  File.open(@generator_path, 'w') do |file|
    file.write ERB.new(File.read(TEMPLATES / 'test_bean_generator.erb')).result
  end
end

@bean_test_path = TEST_JAVA / package_path / "#{@class}Test.java"

if confirm?(@bean_test_path)
  File.open(@bean_test_path, 'w') do |file|
    file.write <<-EOH
  package #{@package};

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class #{@class}Test extends DefaultEntityTestBase<#{@class}>
  {

      @Override
      protected InstanceTester<#{@class}> createEntityInstanceGenerator()
      {
          return new #{@class}Generator(getSeed());
      }
  }
    EOH
  end
end

@dao_test_java = TEST_JAVA / package_path / "#{@class}DAOTest.java"

if confirm?(@dao_test_java)
  File.open(@dao_test_java, 'w') do |file|
    file.write ERB.new(File.read(TEMPLATES / "test_dao.erb")).result
  end
end
