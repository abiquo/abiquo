/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package com.abiquo.nodecollector.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

/**
 * Utility methods to work with annotations.
 * 
 * @author ibarrera
 */
public class AnnotationUtils
{
    /**
     * Find all classes in the given package that has the specified annotation.
     * <p>
     * This method does not scan sub-packages.
     * 
     * @param annotationType The annotation of the classes to find.
     * @param scanPackage The package to scan.
     * @return A list containing all classes in the given package that has the specified annotation.
     * @throws Exception If annotations cannot be scanned.
     */
    public static List<Class< ? extends Object>> findAnnotatedClasses(
        final Class< ? extends Annotation> annotationType, final Package scanPackage)
        throws Exception
    {
        return findAnnotatedClasses(annotationType, scanPackage.getName(), false);
    }

    /**
     * Find all classes in the given package that has the specified annotation.
     * <p>
     * This method does not scan sub-packages.
     * 
     * @param annotationType The annotation of the classes to find.
     * @param scanPackageName The name of the package to scan.
     * @return A list containing all classes in the given package that has the specified annotation.
     * @throws Exception If annotations cannot be scanned.
     */
    public static List<Class< ? extends Object>> findAnnotatedClasses(
        final Class< ? extends Annotation> annotationType, final String scanPackageName)
        throws Exception
    {
        return findAnnotatedClasses(annotationType, scanPackageName, false);
    }

    /**
     * Find all classes in the given package that has the specified annotation.
     * 
     * @param annotationType The annotation of the classes to find.
     * @param scanPackage The package to scan.
     * @param includeSubpackages Boolean indicating if subpackages must be scanned too.
     * @return A list containing all classes in the given package that has the specified annotation.
     * @throws Exception If annotations cannot be scanned.
     */
    public static List<Class< ? extends Object>> findAnnotatedClasses(
        final Class< ? extends Annotation> annotationType, final Package scanPackage,
        final boolean includeSubpackages) throws Exception
    {
        return findAnnotatedClasses(annotationType, scanPackage.getName(), includeSubpackages);
    }

    /**
     * Find all classes in the given package that has the specified annotation.
     * 
     * @param annotationType The annotation of the classes to find.
     * @param scanPackageName The name of the package to scan.
     * @param includeSubpackages Boolean indicating if sub-packages must be scanned too.
     * @return A list containing all classes in the given package that has the specified annotation.
     * @throws Exception If annotations cannot be scanned.
     */
    public static List<Class< ? extends Object>> findAnnotatedClasses(
        final Class< ? extends Annotation> annotationType, final String scanPackageName,
        final boolean includeSubpackages) throws Exception
    {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String scanPattern = buildScanPattern(scanPackageName, includeSubpackages);
        Resource[] resources = resolver.getResources(scanPattern);

        MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
        List<Class< ? extends Object>> classes = new ArrayList<Class< ? extends Object>>();

        for (Resource resource : resources)
        {
            if (resource.isReadable())
            {
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                AnnotationMetadata metadata = reader.getAnnotationMetadata();
                if (metadata.hasAnnotation(annotationType.getName()))
                {
                    Class< ? extends Object> clazz =
                        ClassUtils.forName(metadata.getClassName(), Thread.currentThread()
                            .getContextClassLoader());
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }

    /**
     * Builds the pattern used to scan packages.
     * 
     * @param scanPackageName The name of the package to scan.
     * @param includeSubpackages Boolean indicating if sub-packages must be scanned too.
     * @return The pattern used to scan packages.
     */
    private static String buildScanPattern(final String scanPackageName,
        final boolean includeSubpackages)
    {
        StringBuffer scanPattern = new StringBuffer();

        scanPattern.append(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX);
        scanPattern.append(scanPackageName.replace('.', File.separatorChar));
        scanPattern.append("/");
        if (includeSubpackages)
        {
            scanPattern.append("**/");
        }
        scanPattern.append("*.class");

        return scanPattern.toString();
    }
    
    /**
     * Do nothing. Just for can not instantiate the class with all the methods static.
     */
    private AnnotationUtils()
    {
        
    }
}
