package util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.Shape.Builder;

public class Main {

	public static final String GEOJSON_PATH = "pune.geojson";

	public static final double LATITUDE = 18.437072995653114;
	public static final double LONGITUDE = 73.88178585760585;

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		FeatureCollection featureCollection = loadGeoJson(GEOJSON_PATH);

		if (featureCollection != null) {
			Feature feature = getEnclosingFeature(featureCollection, LATITUDE, LONGITUDE);
			if (feature != null) {
				Map<String, Object> properties = feature.getProperties();
				System.out.println(properties);
			} else {
				System.out.println("Point lies out side the GeoJson");
			}
		} else {
			System.out.println("Could not load the GeoJson");
		}
	}

	public static FeatureCollection loadGeoJson(String geojsonPath) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new File(geojsonPath), FeatureCollection.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Feature getEnclosingFeature(FeatureCollection featureCollection, double latitude, double longitude) {
		List<Feature> features = featureCollection.getFeatures();
		for (Feature feature : features) {
			if (isInside(feature, latitude, longitude)) {
				return feature;
			}
		}
		return null;
	}

	public static boolean isInside(Feature feature, double latitude, double longitude) {
		GeoJsonObject geometry = feature.getGeometry();
		Polygon geojsonPolygon;
		if (!(geometry instanceof Polygon)) {
			return false;
		}
		geojsonPolygon = (Polygon) geometry;
		List<List<LngLatAlt>> polygons = geojsonPolygon.getCoordinates();
		Builder polygonBuilder = new Shape.Builder();
		for (List<LngLatAlt> polygon : polygons) {
			for (LngLatAlt lngLatAlt : polygon) {
				polygonBuilder.addVertex(new Point(lngLatAlt.getLongitude(), lngLatAlt.getLatitude()));
			}
			polygonBuilder.close();
		}
		Shape shape = polygonBuilder.build();
		return shape.contains(new Point(longitude, latitude));
	}

}
