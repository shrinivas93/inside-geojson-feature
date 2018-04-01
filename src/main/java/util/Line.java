package util;

public class Line {

	private final Point _start;
	private final Point _end;
	private double _a = Double.NaN;
	private double _b = Double.NaN;
	private boolean _vertical = false;

	public Line(Point start, Point end) {
		_start = start;
		_end = end;

		if (_end.x - _start.x != 0) {
			_a = ((_end.y - _start.y) / (_end.x - _start.x));
			_b = _start.y - _a * _start.x;
		} else {
			_vertical = true;
		}
	}

	public boolean isInside(Point point) {
		double maxX = _start.x > _end.x ? _start.x : _end.x;
		double minX = _start.x < _end.x ? _start.x : _end.x;
		double maxY = _start.y > _end.y ? _start.y : _end.y;
		double minY = _start.y < _end.y ? _start.y : _end.y;

		if ((point.x >= minX && point.x <= maxX) && (point.y >= minY && point.y <= maxY)) {
			return true;
		}
		return false;
	}

	public boolean isVertical() {
		return _vertical;
	}

	public double getA() {
		return _a;
	}

	public double getB() {
		return _b;
	}

	public Point getStart() {
		return _start;
	}

	public Point getEnd() {
		return _end;
	}

	@Override
	public String toString() {
		return String.format("%s-%s", _start.toString(), _end.toString());
	}
}