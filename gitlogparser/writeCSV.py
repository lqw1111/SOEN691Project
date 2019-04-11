import csv
import string


def readCSV():
    with open('anti-pattern.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        line_count = 0
        for row in csv_reader:
            if line_count == 0:
                print(f'Column names are {", ".join(row)}')
                line_count += 1
            # print(f'\t{row["Kind"]} , {row["Name"]},{row["AvgCyclomatic"]}.')
            line_count += 1

        print(f'Processed {line_count} lines.')


def writeCSV():
    with open('writeCSV.csv', mode='a') as csv_file:
        fieldnames = ['Kind', 'Name', 'AvgCyclomatic', 'AvgCyclomaticModified', 'AvgCyclomaticStrict', 'AvgEssential',
                      'AvgLine', 'AvgLineBlank', 'AvgLineCode', 'AvgLineComment', 'CountClassBase',
                      'CountClassCoupled', 'CountClassDerived', 'CountDeclClass', 'CountDeclClassMethod',
                      'CountDeclClassVariable', 'CountDeclExecutableUnit', 'CountDeclFile',
                      'CountDeclFunction', 'CountDeclInstanceMethod', 'CountDeclInstanceVariable', 'CountDeclMethod',
                      'CountDeclMethodAll', 'CountDeclMethodDefault',
                      'CountDeclMethodPrivate', 'CountDeclMethodProtected', 'CountDeclMethodPublic', 'CountInput',
                      'CountLine', 'CountLineBlank', 'CountLineCode', 'CountLineCodeDecl',
                      'CountLineCodeExe', 'CountLineComment', 'CountOutput', 'CountPath', 'CountPathLog',
                      'CountSemicolon', 'CountStmt', 'CountStmtDecl', 'CountStmtExe', 'Cyclomatic',
                      'CyclomaticModified', 'CyclomaticStrict', 'Essential', 'Knots', 'MaxCyclomatic',
                      'MaxCyclomaticModified', 'MaxCyclomaticStrict', 'MaxEssential', 'MaxEssentialKnots',
                      'MaxInheritanceTree', 'MaxNesting', 'MinEssentialKnots', 'PercentLackOfCohesion',
                      'RatioCommentToCode', 'SumCyclomatic', 'SumCyclomaticModified', 'SumCyclomaticStrict',
                      'SumEssential', 'aa']
        writer = csv.DictWriter(csv_file, fieldnames=fieldnames)

        writer.writeheader()
        writer.writerow({'aa': 'aa'})
        # writer.writerow({'emp_name': 'Erica Meyers', 'dept': 'IT', 'birth_month': 'March'})


def read_writeCSV(readCSV_path, writeCSV_path, file_dict):
    with open('oldCSV.csv', mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        with open('newCSV.csv', 'w') as new_CSV:
            fieldnames = ['Kind', 'Name', 'AvgCyclomatic', 'AvgCyclomaticModified', 'AvgCyclomaticStrict',
                          'AvgEssential', 'AvgLine', 'AvgLineBlank', 'AvgLineCode', 'AvgLineComment', 'CountClassBase',
                          'CountClassCoupled', 'CountClassDerived', 'CountDeclClass', 'CountDeclClassMethod',
                          'CountDeclClassVariable', 'CountDeclExecutableUnit', 'CountDeclFile',
                          'CountDeclFunction', 'CountDeclInstanceMethod', 'CountDeclInstanceVariable',
                          'CountDeclMethod', 'CountDeclMethodAll', 'CountDeclMethodDefault',
                          'CountDeclMethodPrivate', 'CountDeclMethodProtected', 'CountDeclMethodPublic', 'CountInput',
                          'CountLine', 'CountLineBlank', 'CountLineCode', 'CountLineCodeDecl',
                          'CountLineCodeExe', 'CountLineComment', 'CountOutput', 'CountPath', 'CountPathLog',
                          'CountSemicolon', 'CountStmt', 'CountStmtDecl', 'CountStmtExe', 'Cyclomatic',
                          'CyclomaticModified', 'CyclomaticStrict', 'Essential', 'Knots', 'MaxCyclomatic',
                          'MaxCyclomaticModified', 'MaxCyclomaticStrict', 'MaxEssential', 'MaxEssentialKnots',
                          'MaxInheritanceTree', 'MaxNesting', 'MinEssentialKnots', 'PercentLackOfCohesion',
                          'RatioCommentToCode', 'SumCyclomatic', 'SumCyclomaticModified', 'SumCyclomaticStrict',
                          'SumEssential', 'TotalNumberOfChanges', 'TotalCodeChurn', 'NumberOfUniqueAuthors',
                          'Pre-releaseDefects', 'post-releaseDefects']
            writer = csv.DictWriter(new_CSV, fieldnames=fieldnames)
            writer.writeheader()

            # for row in csv_reader:
            #     if row['Name'].__contains__('test') or row['Name'].__contains__('Test'):
            #         continue
            #
            #     if row["Name"] in file_dict:
            #         newOne = {'TotalNumberOfChanges': file_dict[row["Name"]].get_num_changes(), 'TotalCodeChurn': file_dict[row["Name"]].get_code_churn(),'NumberOfUniqueAuthors': file_dict[row["Name"]].get_num_authors(),'Pre-releaseDefects': file_dict[row["Name"]].get_num_changes()}
            #         merge_row = {**row, **newOne}
            #         writer.writerow(merge_row)
            #     else:
            #         newOne = {'TotalNumberOfChanges':'0', 'TotalCodeChurn': '0','NumberOfUniqueAuthors': '0', 'Pre-releaseDefects':'0'}
            #         merge_row = {**row, **newOne}
            #         writer.writerow(merge_row)

            for row in csv_reader:
                if row["Name"] in file_dict:
                    newOne = {'post-releaseDefects': file_dict[row["Name"]].get_num_changes()}
                    merge_row = {**row, **newOne}
                    writer.writerow(merge_row)
                else:
                    newOne = {'post-releaseDefects': '0'}
                    merge_row = {**row, **newOne}
                    writer.writerow(merge_row)


def merge_two_csv(base_csv, merge_csv, new_file):
    newfield = ['overcatch', 'overcatch_abort', 'catch_donothing', 'catch_returnnull', 'catchgeneric',
                'destructivewrapping', 'dummy_handler', 'ignoring_interrupted_exception',
                'incomplete_implementation',
                'log_returnnull', 'log_throw', 'multiple_line_log', 'nestedtry', 'rely_getclause',
                'throw_in_finally',
                'subsumption', 'specific', 'unrecover_exception', 'recover_exception', 'tryquantity',
                'catchquantity',
                'log_try', 'loc_catch', 'sloc_try', 'sloc_catch', 'try_in_declaration', 'try_in_condition',
                'try_in_loop',
                'try_in_eh', 'try_in_other', 'invoked_methods', 'actions_abort', 'actions_continue',
                'actions_default',
                'actions_empty', 'actions_log', 'actions_method', 'actions_nestedtry', 'actions_return',
                'actions_throwcurrent',
                'actions_thrownew', 'actions_throwwrapping', 'actions_todo', 'actions_abort_percentage',
                'actions_continue_percentage',
                'actions_default_percentage', 'actions_empty_percentage', 'actions_log_percentage',
                'this.actions_method_percentage',
                'actions_nestedtry_percentage', 'actions_return_percentage', 'actions_throwcurrent_percentage',
                'actions_thrownew_percentage',
                'actions_throwwrap_percentage', 'actions_todo_percentage', 'subsumption_percentage',
                'specific_percentage', 'overcatch_percentage',
                'overcatch_abort_percentage', 'catch_donothing_percentage', 'catch_returnnull_percentage',
                'catchgeneric_percentage',
                'destructivewrapping_percentage', 'dummy_handler_percentage',
                'ignoring_interrupted_exception_percentage',
                'incomplete_implementation_percentage', 'log_returnnull_percentage', 'log_throw_percentage',
                'multiple_line_log_percentage',
                'nestedtry_percentage', 'rely_getclause_percentage', 'throw_in_finally_percentage']
    row_with_zero = {}
    for field in newfield:
        row_with_zero[field] = '0'
    print(row_with_zero)

    with open(base_csv, mode='r+') as base_csv_file:
        base = csv.DictReader(base_csv_file)

        with open(new_file, mode='w') as aftermerge:
            fieldnames = ['Kind', 'Name', 'AvgCyclomatic', 'AvgCyclomaticModified', 'AvgCyclomaticStrict',
                          'AvgEssential', 'AvgLine', 'AvgLineBlank', 'AvgLineCode', 'AvgLineComment',
                          'CountClassBase',
                          'CountClassCoupled', 'CountClassDerived', 'CountDeclClass', 'CountDeclClassMethod',
                          'CountDeclClassVariable', 'CountDeclExecutableUnit', 'CountDeclFile',
                          'CountDeclFunction', 'CountDeclInstanceMethod', 'CountDeclInstanceVariable',
                          'CountDeclMethod', 'CountDeclMethodAll', 'CountDeclMethodDefault',
                          'CountDeclMethodPrivate', 'CountDeclMethodProtected', 'CountDeclMethodPublic',
                          'CountInput',
                          'CountLine', 'CountLineBlank', 'CountLineCode', 'CountLineCodeDecl',
                          'CountLineCodeExe', 'CountLineComment', 'CountOutput', 'CountPath', 'CountPathLog',
                          'CountSemicolon', 'CountStmt', 'CountStmtDecl', 'CountStmtExe', 'Cyclomatic',
                          'CyclomaticModified', 'CyclomaticStrict', 'Essential', 'Knots', 'MaxCyclomatic',
                          'MaxCyclomaticModified', 'MaxCyclomaticStrict', 'MaxEssential', 'MaxEssentialKnots',
                          'MaxInheritanceTree', 'MaxNesting', 'MinEssentialKnots', 'PercentLackOfCohesion',
                          'RatioCommentToCode', 'SumCyclomatic', 'SumCyclomaticModified', 'SumCyclomaticStrict',
                          'SumEssential', 'TotalNumberOfChanges', 'TotalCodeChurn', 'NumberOfUniqueAuthors',
                          'Pre-releaseDefects', 'post-releaseDefects',
                          'overcatch', 'overcatch_abort', 'catch_donothing', 'catch_returnnull', 'catchgeneric',
                          'destructivewrapping', 'dummy_handler', 'ignoring_interrupted_exception',
                          'incomplete_implementation',
                          'log_returnnull', 'log_throw', 'multiple_line_log', 'nestedtry', 'rely_getclause',
                          'throw_in_finally',
                          'subsumption', 'specific', 'unrecover_exception', 'recover_exception', 'tryquantity',
                          'catchquantity',
                          'log_try', 'loc_catch', 'sloc_try', 'sloc_catch', 'try_in_declaration', 'try_in_condition',
                          'try_in_loop',
                          'try_in_eh', 'try_in_other', 'invoked_methods', 'actions_abort', 'actions_continue',
                          'actions_default',
                          'actions_empty', 'actions_log', 'actions_method', 'actions_nestedtry', 'actions_return',
                          'actions_throwcurrent',
                          'actions_thrownew', 'actions_throwwrapping', 'actions_todo', 'actions_abort_percentage',
                          'actions_continue_percentage',
                          'actions_default_percentage', 'actions_empty_percentage', 'actions_log_percentage',
                          'this.actions_method_percentage',
                          'actions_nestedtry_percentage', 'actions_return_percentage',
                          'actions_throwcurrent_percentage', 'actions_thrownew_percentage',
                          'actions_throwwrap_percentage', 'actions_todo_percentage', 'subsumption_percentage',
                          'specific_percentage', 'overcatch_percentage',
                          'overcatch_abort_percentage', 'catch_donothing_percentage', 'catch_returnnull_percentage',
                          'catchgeneric_percentage',
                          'destructivewrapping_percentage', 'dummy_handler_percentage',
                          'ignoring_interrupted_exception_percentage',
                          'incomplete_implementation_percentage', 'log_returnnull_percentage', 'log_throw_percentage',
                          'multiple_line_log_percentage',
                          'nestedtry_percentage', 'rely_getclause_percentage', 'throw_in_finally_percentage']

            writer = csv.DictWriter(aftermerge, fieldnames=fieldnames)

            writer.writeheader()

            for baserow in base:

                if baserow['Name'].__contains__('test'):
                    continue

                find = False
                addRow = {}

                with open(merge_csv, mode='r') as merge_csv_file:
                    merge = csv.DictReader(merge_csv_file)
                    for mergerow in merge:
                        # print(baserow["Name"])
                        # print(mergerow["file_name"])
                        # print(baserow["Name"].__contains__(mergerow["file_name"]))

                        if baserow['Name'].__contains__(mergerow['file_name']):
                            find = True
                            print(baserow["Name"])
                            # print(mergerow["file_name"])
                            mergerow.pop('file_name')
                            addRow = mergerow
                            # print(11)

                    if find:
                        # print('find')
                        newRow = {**baserow, **addRow}
                        writer.writerow(newRow)
                    else:
                        newRow = {**baserow, **row_with_zero}
                        writer.writerow(newRow)

                merge_csv_file.close()



merge_two_csv('./newCSV.csv', './anti-pattern.csv', 'finalCSV.csv')

# readCSV()
