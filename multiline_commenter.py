import fileinput
import re

single_line_comment = re.compile(r'//(.*)$')

for line in fileinput.input():
    match = single_line_comment.match(line)
    if match:
        print('/*{} */'.format(match.group(1)))
    else:
        print(line, end='')
