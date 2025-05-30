import os

def combine_files(output_file='combined_files.txt'):
    current_dir = os.path.abspath('.')
    output_path = os.path.abspath(output_file)
    
    with open(output_file, 'w', encoding='utf-8') as outfile:
        for root, dirs, files in os.walk('.'):
            if os.path.abspath(root) == os.path.dirname(output_path):
                if output_file in files:
                    files.remove(output_file)

            for file in sorted(files):
                file_path = os.path.join(root, file)
                abs_path = os.path.abspath(file_path)

                if abs_path == output_path:
                    continue

                outfile.write(f'{"="*50}\n')
                outfile.write(f'路径：{file_path}\n')
                outfile.write(f'{"="*50}\n')
                
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
                        content = infile.read()
                        outfile.write(content)
                        if not content.endswith('\n'):
                            outfile.write('\n')
                except Exception as e:
                    outfile.write(f'!!! 读取失败：{str(e)}\n')
                outfile.write('\n\n')
    
    print(f'GG：{output_file}')

if __name__ == '__main__':
    combine_files()
