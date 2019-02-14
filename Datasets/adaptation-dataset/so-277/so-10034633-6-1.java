public class foo {
    public boolean MoveNext() {
        _number = 0;
        _suffix = "";
        _hasValue = false;

        // No more characters
        if (_position >= _length)
            return false;

        _hasValue = true;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c < '0' || c > '9') break;
            _number = _number * 10 + (c - '0');
            _position++;
        }

        int suffixStart = _position;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c == '.') break;
            _position++;
        }

        _suffix = _versionString.substring(suffixStart, _position);

        if (_position < _length) _position++;

        return true;
    }
}