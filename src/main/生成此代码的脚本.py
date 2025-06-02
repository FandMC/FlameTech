import os
import argparse
import sys

def should_ignore_file(file_path, max_size_mb=10):
    """检查是否应该忽略该文件"""
    # 图片文件扩展名
    image_extensions = {
        '.jpg', '.jpeg', '.png', '.gif', '.bmp', '.tiff', '.tif', '.webp', '.svg', '.ico', '.cur',
        '.psd', '.ai', '.eps', '.raw', '.cr2', '.nef', '.orf', '.sr2', '.dng',  # 专业图像格式
        '.heic', '.heif', '.avif', '.jxl',  # 新型图像格式
    }

    # 字体文件扩展名
    font_extensions = {
        '.ttf', '.otf', '.woff', '.woff2', '.eot',  # 网页字体
        '.ttc', '.pfb', '.pfm', '.afm', '.fon', '.fnt',  # 桌面字体
    }

    # 音频文件扩展名
    audio_extensions = {
        '.mp3', '.wav', '.flac', '.aac', '.ogg', '.wma', '.m4a', '.opus',
        '.aiff', '.au', '.mid', '.midi', '.mod', '.s3m', '.xm', '.it',
    }

    # 视频文件扩展名
    video_extensions = {
        '.mp4', '.avi', '.mov', '.mkv', '.wmv', '.flv', '.webm', '.m4v',
        '.3gp', '.3g2', '.asf', '.rm', '.rmvb', '.vob', '.ts', '.mts',
    }

    # 压缩文件扩展名
    archive_extensions = {
        '.zip', '.rar', '.7z', '.tar', '.gz', '.bz2', '.xz', '.lz', '.lzma',
        '.cab', '.iso', '.dmg', '.pkg', '.deb', '.rpm', '.msi', '.apk',
    }

    # 可执行文件和库
    executable_extensions = {
        '.exe', '.dll', '.so', '.dylib', '.app', '.deb', '.rpm', '.msi',
        '.com', '.bat', '.cmd', '.scr', '.vbs', '.ps1', '.sh', '.run',
    }

    # 办公文档
    document_extensions = {
        '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx',
        '.odt', '.ods', '.odp', '.rtf', '.pages', '.numbers', '.key',
    }

    # 数据库和二进制文件
    binary_extensions = {
        '.bin', '.dat', '.db', '.sqlite', '.sqlite3', '.mdb', '.accdb',
        '.dbf', '.frm', '.myi', '.myd', '.ibd', '.ldf', '.mdf',
    }

    # 编译文件和缓存
    compiled_extensions = {
        '.pyc', '.pyo', '.pyd', '.pyz', '.pywz',  # Python
        '.class', '.jar', '.war', '.ear',  # Java
        '.o', '.obj', '.lib', '.a', '.lo', '.la',  # C/C++
        '.dcu', '.dcp', '.dcpil', '.dres',  # Delphi
        '.beam',  # Erlang
        '.elc',  # Emacs Lisp
        '.fasl', '.fas', '.lib64fasl',  # Lisp
    }

    # 临时文件和系统文件
    temp_extensions = {
        '.tmp', '.temp', '.bak', '.backup', '.swp', '.swo', '.orig',
        '.DS_Store', '.Thumbs.db', '.desktop.ini', '.directory',
        '.~lock.*', '.*.swp', '.*.swo',
    }

    # 开发工具相关文件
    dev_extensions = {
        '.map', '.min.js', '.min.css',  # 压缩和映射文件
        '.d.ts', '.tsbuildinfo',  # TypeScript
        '.log', '.out', '.err',  # 日志文件
        '.lock', '.pid', '.socket',  # 锁定和进程文件
    }

    # 合并所有需要忽略的扩展名
    all_ignore_extensions = (
            image_extensions | font_extensions | audio_extensions | video_extensions |
            archive_extensions | executable_extensions | document_extensions |
            binary_extensions | compiled_extensions | temp_extensions | dev_extensions
    )


    # 检查文件扩展名
    _, ext = os.path.splitext(file_path.lower())
    if ext in all_ignore_extensions:
        return True

    # 检查文件名模式（用于匹配特殊文件名）
    filename = os.path.basename(file_path).lower()
    ignore_patterns = {
        'thumbs.db', '.ds_store', 'desktop.ini', '.directory',
        'icon\r', '.icon', '.DS_Store',
    }

    if filename in ignore_patterns:
        return True

    # 检查以特定前缀开头的文件
    ignore_prefixes = {'.~lock.', '~',}

    # 检查文件大小
    try:
        file_size = os.path.getsize(file_path)
        max_size_bytes = max_size_mb * 1024 * 1024
        if file_size > max_size_bytes:
            return True
    except OSError:
        return True

    return False

def should_ignore_directory(dir_path, ignore_dirs):
    """检查是否应该忽略该目录"""
    dir_name = os.path.basename(dir_path)

    # 默认忽略的目录
    default_ignore_dirs = {
        # 版本控制
        '.git', '.svn', '.hg', '.bzr', '.cvs',

        # Python 相关
        '__pycache__', '.pytest_cache', '.tox', '.coverage',
        'venv', 'env', '.env', '.venv', 'virtualenv',
        '.mypy_cache', '.pytype', 'site-packages',

        # Node.js 相关
        'node_modules', '.npm', '.yarn', '.pnpm-store',
        '.next', '.nuxt', 'dist', 'build',

        # IDE 和编辑器配置
        '.vscode', '.idea', '.vs', '.sublime-project', '.sublime-workspace',
        '.atom', '.brackets.json', '.project', '.settings',

        # 构建和输出目录
        'build', 'dist', 'target', 'bin', 'obj', 'out',
        'release', 'debug', 'tmp', 'temp', 'cache',

        # 系统文件和缓存
        '.DS_Store', 'Thumbs.db', '.Spotlight-V100', '.Trashes',
        '.fseventsd', '.DocumentRevisions-V100', '.TemporaryItems',

        # 日志和临时文件
        'logs', 'log', 'tmp', 'temp', '.tmp', '.temp',

        # 测试和覆盖率
        'coverage', 'test-results', 'test-reports',
        '.nyc_output', 'lcov-report', 'htmlcov',

        # 包管理器
        '.bundle', 'vendor', 'Pods', 'Carthage',

        # 其他常见目录
        'backup', 'backups', '.sass-cache', '.gradle',
        '.m2', '.ivy2', '.sbt', 'bower_components',
    }

    # 检查是否在默认忽略列表中
    if dir_name in default_ignore_dirs:
        return True

    # 检查是否在用户指定的忽略列表中
    if ignore_dirs:
        for ignore_dir in ignore_dirs:
            if dir_name == ignore_dir or dir_path.endswith(ignore_dir):
                return True

    return False

def combine_files(output_file='combined_files.txt', ignore_dirs=None, max_size_mb=10, verbose=False):
    """
    合并当前目录及子目录下的所有文本文件

    Args:
        output_file: 输出文件名
        ignore_dirs: 要忽略的目录列表
        max_size_mb: 文件大小限制（MB）
        verbose: 是否显示详细信息
    """
    current_dir = os.path.abspath('.')
    output_path = os.path.abspath(output_file)

    if ignore_dirs is None:
        ignore_dirs = []

    processed_files = 0
    ignored_files = 0

    print(f"开始合并文件到: {output_file}")
    print(f"文件大小限制: {max_size_mb}MB")
    if ignore_dirs:
        print(f"忽略目录: {', '.join(ignore_dirs)}")
    print("-" * 50)

    with open(output_file, 'w', encoding='utf-8') as outfile:
        for root, dirs, files in os.walk('.'):
            # 过滤要忽略的目录
            dirs[:] = [d for d in dirs if not should_ignore_directory(os.path.join(root, d), ignore_dirs)]

            # 跳过输出文件所在目录的输出文件
            if os.path.abspath(root) == os.path.dirname(output_path):
                if output_file in files:
                    files.remove(output_file)

            for file in sorted(files):
                file_path = os.path.join(root, file)
                abs_path = os.path.abspath(file_path)

                # 跳过输出文件本身
                if abs_path == output_path:
                    continue

                # 检查是否应该忽略该文件
                if should_ignore_file(file_path, max_size_mb):
                    ignored_files += 1
                    if verbose:
                        print(f"忽略文件: {file_path}")
                    continue

                # 写入文件分隔符和路径
                outfile.write(f'{"="*50}\n')
                outfile.write(f'路径：{file_path}\n')
                outfile.write(f'{"="*50}\n')

                try:
                    with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
                        content = infile.read()
                        outfile.write(content)
                        if not content.endswith('\n'):
                            outfile.write('\n')
                    processed_files += 1
                    if verbose:
                        print(f"处理文件: {file_path}")
                except Exception as e:
                    outfile.write(f'!!! 读取失败：{str(e)}\n')
                    if verbose:
                        print(f"读取失败: {file_path} - {str(e)}")

                outfile.write('\n\n')

    print("-" * 50)
    print(f'完成！输出文件: {output_file}')
    print(f'处理文件数: {processed_files}')
    print(f'忽略文件数: {ignored_files}')

def main():
    parser = argparse.ArgumentParser(description='合并目录中的文本文件')
    parser.add_argument('-o', '--output', default='combined_files.txt',
                        help='输出文件名 (默认: combined_files.txt)')
    parser.add_argument('-i', '--ignore-dirs', nargs='*', default=[],
                        help='要忽略的目录名称 (可指定多个)')
    parser.add_argument('-s', '--max-size', type=int, default=10,
                        help='文件大小限制，单位MB (默认: 10)')
    parser.add_argument('-v', '--verbose', action='store_true',
                        help='显示详细处理信息')

    args = parser.parse_args()

    try:
        combine_files(
            output_file=args.output,
            ignore_dirs=args.ignore_dirs,
            max_size_mb=args.max_size,
            verbose=args.verbose
        )
    except KeyboardInterrupt:
        print("\n操作被用户中断")
        sys.exit(1)
    except Exception as e:
        print(f"错误: {str(e)}")
        sys.exit(1)

def should_ignore_directory(dir_path, ignore_dirs):
    """检查是否应该忽略该目录"""
    dir_name = os.path.basename(dir_path)

    # 默认忽略的目录
    default_ignore_dirs = {
        '.git', '.svn', '.hg',  # 版本控制
        '__pycache__', '.pytest_cache',  # Python 缓存
        'node_modules', '.npm',  # Node.js
        '.vscode', '.idea', '.vs',  # IDE 配置
        'venv', 'env', '.env',  # Python 虚拟环境
        'build', 'dist', 'target',  # 构建输出
        '.DS_Store', 'Thumbs.db',  # 系统文件
        'logs', 'log',  # 日志目录
    }

    # 检查是否在默认忽略列表中
    if dir_name in default_ignore_dirs:
        return True

    # 检查是否在用户指定的忽略列表中
    if ignore_dirs:
        for ignore_dir in ignore_dirs:
            if dir_name == ignore_dir or dir_path.endswith(ignore_dir):
                return True

    return False

def combine_files(output_file='combined_files.txt', ignore_dirs=None, max_size_mb=10, verbose=False):
    """
    合并当前目录及子目录下的所有文本文件

    Args:
        output_file: 输出文件名
        ignore_dirs: 要忽略的目录列表
        max_size_mb: 文件大小限制（MB）
        verbose: 是否显示详细信息
    """
    current_dir = os.path.abspath('.')
    output_path = os.path.abspath(output_file)

    if ignore_dirs is None:
        ignore_dirs = []

    processed_files = 0
    ignored_files = 0

    print(f"开始合并文件到: {output_file}")
    print(f"文件大小限制: {max_size_mb}MB")
    if ignore_dirs:
        print(f"忽略目录: {', '.join(ignore_dirs)}")
    print("-" * 50)

    with open(output_file, 'w', encoding='utf-8') as outfile:
        for root, dirs, files in os.walk('.'):
            # 过滤要忽略的目录
            dirs[:] = [d for d in dirs if not should_ignore_directory(os.path.join(root, d), ignore_dirs)]

            # 跳过输出文件所在目录的输出文件
            if os.path.abspath(root) == os.path.dirname(output_path):
                if output_file in files:
                    files.remove(output_file)

            for file in sorted(files):
                file_path = os.path.join(root, file)
                abs_path = os.path.abspath(file_path)

                # 跳过输出文件本身
                if abs_path == output_path:
                    continue

                # 检查是否应该忽略该文件
                if should_ignore_file(file_path, max_size_mb):
                    ignored_files += 1
                    if verbose:
                        print(f"忽略文件: {file_path}")
                    continue

                # 写入文件分隔符和路径
                outfile.write(f'{"="*50}\n')
                outfile.write(f'路径：{file_path}\n')
                outfile.write(f'{"="*50}\n')

                try:
                    with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
                        content = infile.read()
                        outfile.write(content)
                        if not content.endswith('\n'):
                            outfile.write('\n')
                    processed_files += 1
                    if verbose:
                        print(f"处理文件: {file_path}")
                except Exception as e:
                    outfile.write(f'!!! 读取失败：{str(e)}\n')
                    if verbose:
                        print(f"读取失败: {file_path} - {str(e)}")

                outfile.write('\n\n')

    print("-" * 50)
    print(f'完成！输出文件: {output_file}')
    print(f'处理文件数: {processed_files}')
    print(f'忽略文件数: {ignored_files}')

def main():
    parser = argparse.ArgumentParser(description='合并目录中的文本文件')
    parser.add_argument('-o', '--output', default='combined_files.txt',
                        help='输出文件名 (默认: combined_files.txt)')
    parser.add_argument('-i', '--ignore-dirs', nargs='*', default=[],
                        help='要忽略的目录名称 (可指定多个)')
    parser.add_argument('-s', '--max-size', type=int, default=10,
                        help='文件大小限制，单位MB (默认: 10)')
    parser.add_argument('-v', '--verbose', action='store_true',
                        help='显示详细处理信息')

    args = parser.parse_args()

    try:
        combine_files(
            output_file=args.output,
            ignore_dirs=args.ignore_dirs,
            max_size_mb=args.max_size,
            verbose=args.verbose
        )
    except KeyboardInterrupt:
        print("\n操作被用户中断")
        sys.exit(1)
    except Exception as e:
        print(f"错误: {str(e)}")
        sys.exit(1)

if __name__ == '__main__':
    main()