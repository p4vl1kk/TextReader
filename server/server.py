from flask import Flask, request, jsonify, render_template, redirect, send_from_directory, json
from pathlib import Path
import logging

app = Flask(__name__)
UPLOAD_FOLDER = 'uploads'
Path(UPLOAD_FOLDER).mkdir(parents=True, exist_ok=True)

logging.basicConfig(level=logging.DEBUG)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    logging.debug('Upload route called')
    if request.method == 'POST':
        if 'file' not in request.files:
            logging.error('No file part in the request')
            return jsonify({"message": "No file part in the request"}), 400

        file = request.files['file']
        if file.filename == '':
            logging.error('No selected file')
            return jsonify({"message": "No selected file"}), 400

        file_path = Path(UPLOAD_FOLDER) / file.filename

        try:
            file.save(file_path)
            logging.debug(f'File saved to {file_path}')
        except Exception as e:
            logging.error(f'Error while saving file: {e}')
            return jsonify({"message": "Failed to save file"}), 500

        return jsonify({"message": f"File '{file.filename}' uploaded successfully"})
    return render_template('upload.html')

@app.route('/files', methods=['GET'])
@app.route('/files/<path:directory>', methods=['GET'])
def list_files(directory=""):
    logging.debug(f'List files route called for {directory}')
    base_path = Path(UPLOAD_FOLDER) / directory
    if not base_path.exists() or not base_path.is_dir():
        return jsonify({"message": "Directory not found"}), 404

    file_tree = build_file_tree(base_path)
    return render_template('files.html', file_tree=file_tree, directory=directory)


@app.route('/file/<path:filename>', methods=['GET'])
def view_file(filename):
    logging.debug(f'View file route called for {filename}')

    file_path = Path(UPLOAD_FOLDER) / filename

    if file_path.exists() and file_path.is_file():
        file_ext = file_path.suffix.lower()
        if file_ext in ['.txt', '.json', '.log']:
            with open(file_path, 'r', encoding='utf-8') as f:
                file_content = f.read()
            return render_template('view_file.html', filename=filename, file_content=file_content)
        else:
            return send_from_directory(file_path.parent, file_path.name, as_attachment=True)
    else:
        return jsonify({"message": "File not found"}), 404


@app.route('/delete_file/<path:filename>', methods=['POST'])
def delete_file(filename):
    logging.debug(f'Delete file route called for {filename}')

    file_path = Path(UPLOAD_FOLDER) / filename

    if file_path.exists() and file_path.is_file():
        try:
            file_path.unlink()
            logging.debug(f'File {filename} deleted')
            return redirect(request.referrer)
        except Exception as e:
            logging.error(f'Error deleting file: {e}')
            return jsonify({"message": "Failed to delete file"}), 500
    else:
        return jsonify({"message": "File not found"}), 404

@app.route('/files', methods=['GET'])
def get_version():
    version_file = Path(UPLOAD_FOLDER) / "app_config.json"
    if version_file.exists():
        with open(version_file, 'r', encoding='utf-8') as f:
            return jsonify(json.load(f))
    else:
        return jsonify({"message": "Version file not found"}), 404


def build_file_tree(path):
    tree = []
    for item in path.iterdir():
        if item.is_dir():
            tree.append({
                "type": "directory",
                "name": item.name,
                "path": str(item.relative_to(UPLOAD_FOLDER))
            })
        else:
            tree.append({
                "type": "file",
                "name": item.name,
                "path": str(item.relative_to(UPLOAD_FOLDER))
            })
    return tree

if __name__ == '__main__':
    app.run(debug=True)
