#!/usr/bin/python 

import re
import os
import sys

dirtomove = os.path.dirname(sys.argv[0])
if dirtomove != "":
    os.chdir(dirtomove)

# Create the needed directories
if not os.path.exists('./test'):
    os.mkdir('./test')

ofile = open('test/kinton-schema-test.sql', 'w')

# Create the base schema
ifile = open('kinton-schema.sql', 'r')
replaced_string = ifile.read().replace('kinton','kinton_test')
replaced_string = re.compile(r'LOCK\s*TABLES[^;]+;\s+(INSERT[^;]+;\s+)*UNLOCK\s*TABLES\s*;', 
        re.IGNORECASE).sub((lambda x: ''), replaced_string)
# replaced_string = replaced_string.replace('source accounting/accounting-apply-patch.sql','')
ofile.write(replaced_string)
ifile.close()

# Replace the accounting schema
# ifile = open('accounting/accounting-schema.sql','r')
# replaced_string_accounting1 = ifile.read().replace('kinton','kinton_test')
# ofile.write(replaced_string_accounting1)
# ifile.close()

# Replace the accounting procedures
# ifile = open('accounting/accounting-procedures.sql','r')
# replaced_string_accounting2 = ifile.read().replace('kinton','kinton_test')
# ofile.write(replaced_string_accounting2)
# ifile.close()

# Replace the accounting triggers
# ifile = open('accounting/accounting-triggers.sql','r')
# replaced_string_accounting3 = ifile.read().replace('kinton','kinton_test')
# ofile.write(replaced_string_accounting3)
# ifile.close()

# Replace the additional views
# ifile = open('accounting/additional_views.sql','r')
# replaced_string_accounting4 = ifile.read().replace('kinton','kinton_test')
# ofile.write(replaced_string_accounting4)
# ifile.close()

ofile.close()
