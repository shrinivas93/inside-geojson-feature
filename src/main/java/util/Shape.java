package util;

import java.util.ArrayList;
import java.util.List;

public class Shape {

	private final BoundingBox _boundingBox;
	private final List<Line> _sides;

	private Shape(List<Line> sides, BoundingBox boundingBox) {
		_sides = sides;
		_boundingBox = boundingBox;
	}

	public static Builder Builder() {
		return new Builder();
	}

	public static class Builder {
		private List<Point> _vertexes = new ArrayList<Point>();
		private List<Line> _sides = new ArrayList<Line>();
		private BoundingBox _boundingBox = null;

		private boolean _firstPoint = true;
		private boolean _isClosed = false;

		public Builder addVertex(Point point) {
			if (_isClosed) {
				_vertexes = new ArrayList<Point>();
				_isClosed = false;
			}

			updateBoundingBox(point);
			_vertexes.add(point);

			if (_vertexes.size() > 1) {
				Line Line = new Line(_vertexes.get(_vertexes.size() - 2), point);
				_sides.add(Line);
			}

			return this;
		}

		public Builder close() {
			validate();

			_sides.add(new Line(_vertexes.get(_vertexes.size() - 1), _vertexes.get(0)));
			_isClosed = true;

			return this;
		}

		public Shape build() {
			validate();

			if (!_isClosed) {
				_sides.add(new Line(_vertexes.get(_vertexes.size() - 1), _vertexes.get(0)));
			}

			Shape shape = new Shape(_sides, _boundingBox);
			return shape;
		}

		private void updateBoundingBox(Point point) {
			if (_firstPoint) {
				_boundingBox = new BoundingBox();
				_boundingBox.xMax = point.x;
				_boundingBox.xMin = point.x;
				_boundingBox.yMax = point.y;
				_boundingBox.yMin = point.y;

				_firstPoint = false;
			} else {
				if (point.x > _boundingBox.xMax) {
					_boundingBox.xMax = point.x;
				} else if (point.x < _boundingBox.xMin) {
					_boundingBox.xMin = point.x;
				}
				if (point.y > _boundingBox.yMax) {
					_boundingBox.yMax = point.y;
				} else if (point.y < _boundingBox.yMin) {
					_boundingBox.yMin = point.y;
				}
			}
		}

		private void validate() {
			if (_vertexes.size() < 3) {
				throw new RuntimeException("Shape must have at least 3 points");
			}
		}
	}

	public boolean contains(Point point) {
		if (inBoundingBox(point)) {
			Line ray = createRay(point);
			int intersection = 0;
			for (Line side : _sides) {
				if (intersect(ray, side)) {
					intersection++;
				}
			}

			if (intersection % 2 != 0) {
				return true;
			}
		}
		return false;
	}

	public List<Line> getSides() {
		return _sides;
	}

	private boolean intersect(Line ray, Line side) {
		Point intersectPoint = null;

		if (!ray.isVertical() && !side.isVertical()) {
			if (ray.getA() - side.getA() == 0) {
				return false;
			}

			double x = ((side.getB() - ray.getB()) / (ray.getA() - side.getA()));
			double y = side.getA() * x + side.getB();
			intersectPoint = new Point(x, y);
		} else if (ray.isVertical() && !side.isVertical()) {
			double x = ray.getStart().x;
			double y = side.getA() * x + side.getB();
			intersectPoint = new Point(x, y);
		} else if (!ray.isVertical() && side.isVertical()) {
			double x = side.getStart().x;
			double y = ray.getA() * x + ray.getB();
			intersectPoint = new Point(x, y);
		} else {
			return false;
		}

		if (side.isInside(intersectPoint) && ray.isInside(intersectPoint)) {
			return true;
		}

		return false;
	}

	private Line createRay(Point point) {
		double epsilon = (_boundingBox.xMax - _boundingBox.xMin) / 10e6;
		Point outsidePoint = new Point(_boundingBox.xMin - epsilon, _boundingBox.yMin);

		Line vector = new Line(outsidePoint, point);
		return vector;
	}

	private boolean inBoundingBox(Point point) {
		if (point.x < _boundingBox.xMin || point.x > _boundingBox.xMax || point.y < _boundingBox.yMin
				|| point.y > _boundingBox.yMax) {
			return false;
		}
		return true;
	}

	private static class BoundingBox {
		public double xMax = Double.NEGATIVE_INFINITY;
		public double xMin = Double.NEGATIVE_INFINITY;
		public double yMax = Double.NEGATIVE_INFINITY;
		public double yMin = Double.NEGATIVE_INFINITY;
	}
}