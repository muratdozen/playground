__author__ = 'Murat Derya Ozen'

PROGRAM_DESCRIPTION = """
Spot the ugliest files in a code repository.
Given a path to a file or a directory, this program parses source files to extract,
pretty print and calculate the number of curly brackets, i.e braces.

*** Samples usages ***

> python pretty_print_braces file --path /root/repo/svn/trunk/SomeJavaClass.java

Assuming SomeJavaClass.java is a simple Java source file containing the following code:
    public class SomeJavaClass {
        public static void main(String[] args) {
            if (args != null && args.length > 2) {
                System.out.println("Hello");
            }
        }
    }

The program would output:
/root/repo/svn/trunk/SomeJavaClass.java
{
    {
        {
        }
    }
}
Depths: [1, 1, 1]
Max depth: 3

The output starts with the file name, followed by the braces in the file pretty-printed,
followed by the number of braces at each level, finally followed by the maximum depth.
This means that the source file contains at most 3 levels of nested braces.
Furthermore; we see that at there is 1 opening and closing brace for each level
(i.e 1 for the class declaration, 1 for the static main method declaration and 1 for the
if statement).


> python pretty_print_braces file --path /root/repo/svn/trunk/SomeJavaClass.java -q

Adding '-q' or '--quiet' switches to quite mode where pretty-printing of the braces are not
included in the output.


> python pretty_print_braces dir -p /root/repo/svn/trunk

This command process the whole directory (recursively) as opposed to a single file. It processes each
source file and prints the ugliest 5. Ugliness is defined by having the most depth of nested braces.

> python pretty_print_braces dir -p /root/repo/svn/trunk --nonrecursive --extensions=cpp,c,h -q -n 3

Process the source files with extensions cpp, c or h in the directory at /root/repo/svn/trunk. Output the
ugliest 3 source files in quiet mode. --nonrecursive indicates that subdirectories of trunk will not be traversed.

It's important to note that braces inside comments or strings will be processed too.
"""

import argparse
import heapq
import re
import os

INDENT = ' ' * 4
LINE_SEPARATOR = os.linesep


def extract_braces(source_code, filename, quiet):
    """ Parses and extracts the braces from source_code string.
    Returns a tuple (max_depth, depths, braces, filename).
    max_depth is the maximum depth of the braces; the deepest level found.
    depths is a list of integers such that depths[i] is the number of
    opening braces at depth i.
    braces is a string containing all braces in source_code pretty printed.
    """
    current_depth, depths, braces = 0, [0], []

    def increment_depth_occurence(d):
        if d < len(depths):
            depths[d] += 1
        else:
            depths.append(1)

    for char in source_code:
        if char === '{':
            if not quiet:
                braces.append((INDENT * current_depth) + '{')
            current_depth += 1
            increment_depth_occurence(current_depth)
        elif char == '}':
            current_depth -= 1
            if not quiet:
                braces.append((INDENT * current_depth) + '}')
    max_depth = len(depths) - 1
    return max_depth, depths[1:], LINE_SEPARATOR.join(braces), filename


def analyze_file(filename, quiet):
    """ Open the file named filename, parse it and
    return the extract_braces(file_content)'s result. """
    file = open(filename, 'r')
    content = file.read()
    result = extract_braces(content, filename, quiet)
    file.close()
    return result


def walk_through_directory(rootpath, recursive, n, file_extensions, quiet):
    """ Taverse the directory rooted at rootpath. Analyze each file.
    Store the greatest n results inside a heap.
    Only files within file_extensions are processed, other are skipped.
    If recursive is True, traversal will include subdirectories of
    rootpath as well.
    Returns the heap containing at most n results. Each element
    is a tuple returned by extract_braces(source_code). Elements
    are compared according to the tuple' first element.
    """
    heap_max_size = n

    def heappush(h, element):
        """ Insert element into h iff heap's max size has not been reached
        or element is greater than the lowest element in the heap. """
        if len(h) < heap_max_size:
            heapq.heappush(h, element)
        else:
            heapq.heappushpop(h, element)

    file_extension_regex = '^.+\\.(' + '|'.join(file_extensions) + ')$'
    file_extension_supported = lambda filename: bool(re.match(file_extension_regex, filename, re.I))
    join = os.path.join
    h = [] # heap to keep track of the greatest n items.

    def process_directory(dirpath, filenames):
        """ Process the files rooted immediately in dirpath,
        without going into dirpath's subdirectories. """
        for filename in filenames:
            if not file_extension_supported(filename): continue
            absolute_file_path = join(dirpath, filename)
            result = analyze_file(absolute_file_path, quiet)
            heappush(h, result)

    if recursive:
        for dirpath, _, filenames in os.walk(rootpath):
            process_directory(dirpath, filenames)
    else:
        process_directory(rootpath, os.listdir(rootpath))
    return h


def get_argparser():
    """ Defines and returns the ArgumentParser for this program. """
    parser = argparse.ArgumentParser(description=PROGRAM_DESCRIPTION)
    sub_parsers = parser.add_subparsers()

    file_sub_parser = sub_parsers.add_parser('file')
    file_sub_parser.add_argument('-p', '--path', required=True,
                                 help='Parse this file to extract and pretty print braces.')
    file_sub_parser.add_argument('-q', '--quiet', action='store_true',
                                 help='Increase output quietness. By default, '
                                      + 'this program prints the file name, maximum depth (deepest level), '
                                      + 'number of opening braces for each depth and the braces pretty '
                                      + 'printed. Supplying \'-q\' will omit pretty printing of the braces')
    file_sub_parser.set_defaults(func=pretty_print_file)

    dir_sub_parser = sub_parsers.add_parser('dir')

    dir_sub_parser.add_argument('-p', '--path', required=True,
                                help='Traverse the files rooted in this directory and all its subdirectories '
                                     + 'and parse each file to extract and pretty print braces.')
    dir_sub_parser.add_argument('--nonrecursive', action='store_true',
                                help='Traverse only the files in dir but not in subdirectories of dir. By '
                                     + 'default, directories are traversed recursively.')
    dir_sub_parser.add_argument('--extensions', default='java,cpp,c,h',
                                help='Only the files with these file extensions are parsed. This optional '
                                     + 'argument can take a single extension or a comma separated list. '
                                     + 'Default is \'java,cpp,c,h\'.')
    dir_sub_parser.add_argument('-n', type=int, default=5,
                                help='Resulting output includes the ugliest \'n\' files. Ugliness is defined by '
                                     + 'the depth of braces. For instance, the Java class defined as '
                                     + '\'class A { void method() { if(true) {return;} } }\' would have a '
                                     + ' depth of 3 as it\'s curly braces go 3 levels deep. By default n=5.')

    dir_sub_parser.add_argument('-q', '--quiet', action='store_true',
                                help='Increase output quietness. By default, '
                                     + 'this program prints the file name, maximum depth (deepest level), '
                                     + 'number of opening braces for each depth and the braces pretty '
                                     + 'printed. Supplying \'-q\' will omit pretty printing of the braces')
    dir_sub_parser.set_defaults(func=pretty_print_dir)

    return parser


def print_result(analysis_result, quiet):
    """ Print analysis_result to STDOUT """
    max_depth, depths, braces, filename = analysis_result
    print filename
    if not quiet:
        print braces
    print 'Depths:', depths
    print 'Max depth:', max_depth


def pretty_print_dir(args):
    heap = walk_through_directory(args.path, not args.nonrecursive, args.n, args.extensions.split(','), args.quiet)
    for i in range(min(len(heap), args.n)):
        analysis_result = heapq.heappop(heap)
        print_result(analysis_result, args.quiet)
        print


def pretty_print_file(args):
    analysis_result = analyze_file(args.path, args.quiet)
    print_result(analysis_result, args.quiet)


def main():
    parser = get_argparser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
