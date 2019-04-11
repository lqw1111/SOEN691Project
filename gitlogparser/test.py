import re
# line = '    YARN-5649. Add REST endpoints for updating application timeouts. Contributed by Rohith Sharma K S'
# print(str.split("...")[1])
# print(str.split("...")[1].split("|"))
# print(str.split("...")[1].split("|")[0].rstrip())
# print(str.split("...")[1].split("|")[1].lstrip().rstrip())
# print(str.split("...")[1].split("|")[1].lstrip().rstrip().split(" ")[0])
# print((line.endswith("+") or line.endswith("-")))
# print(line.__contains__(".java"))
# if (line.endswith("+") or line.endswith("-")) and line.__contains__(".java"):
#     print(line.split("|"))
#     print(line.split("|")[0].lstrip().rstrip())
#     print(line.split("|")[1].lstrip().rstrip())
#     print(line.split("|")[1].lstrip().rstrip().split(' ')[0])
#     # print(re.split('[^0-9]',line))

# print(line.split(" "))
# line = line.lstrip().rstrip()
# print(line.split(' ')[0].strip('.'))



# f = open('./test.txt','r')
#
# for line in f:
#     if line.startswith("commit"):
#         pass
#
#
#

# f1 = open('issue.txt','r')
# f2 = open('test.txt','r')
# set1 = set()
# set2 = set()
# for line in f1:
#     line = line.strip('\n').lstrip().rstrip()
#     set1.add(line)
# for line in f2:
#     line = line.strip('\n').lstrip().rstrip()
#     set2.add(line)
#
# for iss in (set1 - set2):
#     print(iss)

x = { 'apple': 1, 'banana': 2 }

y = { 'banana': 2, 'pear': 11 }

print(x.pop('apple'))
print(x)

newfield = ['overcatch','overcatch_abort','catch_donothing','catch_returnnull','catchgeneric',
                              'destructivewrapping','dummy_handler','ignoring_interrupted_exception','incomplete_implementation',
                              'log_returnnull','log_throw','multiple_line_log','nestedtry','rely_getclause','throw_in_finally',
                              'subsumption','specific','unrecover_exception','recover_exception','tryquantity','catchquantity',
                              'log_try','loc_catch','sloc_try','sloc_catch','try_in_declaration','try_in_condition','try_in_loop',
                              'try_in_eh','try_in_other','invoked_methods','actions_abort','actions_continue','actions_default',
                              'actions_empty','actions_log','actions_method','actions_nestedtry','actions_return','actions_throwcurrent',
                              'actions_thrownew','actions_throwwrapping','actions_todo','actions_abort_percentage','actions_continue_percentage',
                              'actions_default_percentage','actions_empty_percentage','actions_log_percentage','this.actions_method_percentage',
                              'actions_nestedtry_percentage','actions_return_percentage','actions_throwcurrent_percentage','actions_thrownew_percentage',
                              'actions_throwwrap_percentage','actions_todo_percentage','subsumption_percentage','specific_percentage','overcatch_percentage',
                              'overcatch_abort_percentage','catch_donothing_percentage','catch_returnnull_percentage','catchgeneric_percentage',
                              'destructivewrapping_percentage','dummy_handler_percentage','ignoring_interrupted_exception_percentage',
                              'incomplete_implementation_percentage','log_returnnull_percentage','log_throw_percentage','multiple_line_log_percentage',
                              'nestedtry_percentage','rely_getclause_percentage','throw_in_finally_percentage']
newRow = {}
for field in newfield:
    newRow[field] = '0'

print(newRow)

addRow = {}
print(len(addRow) is 0)

a = '/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/JournalSet.java'
b = 'File,hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/JournalSet.java,1,1,1,1,8,0,7,1,,,,20,1,3,72,,73,72,8,73,,13,4,2,54,,749,78,535,168,272,137,,,,192,327,147,180,,,,,,10,10,10,4,,,5,,,0.26,121,121,123,83,0,0,0,0'
print(b.__contains__(a))