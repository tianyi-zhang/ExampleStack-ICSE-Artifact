public class foo{
    public static List<SerializableLocation> getPoints(File gpxFile) throws Exception {
        List<SerializableLocation> points;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        FileInputStream fis = new FileInputStream(gpxFile);
        Document dom = builder.parse(fis);
        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("trkpt");

        points = new ArrayList<>();

        for (int j = 0; j < items.getLength(); j++) {
            Node item = items.item(j);
            NamedNodeMap attrs = item.getAttributes();
            NodeList props = item.getChildNodes();

            Location pt = new Location("test");

            pt.setLatitude(Double.parseDouble(attrs.getNamedItem("lat").getNodeValue()));
            pt.setLongitude(Double.parseDouble(attrs.getNamedItem("lon").getNodeValue()));

            for (int k = 0; k < props.getLength(); k++) {
                Node item2 = props.item(k);
                String name = item2.getNodeName();

                if (name.equalsIgnoreCase("ele")) {
                    pt.setAltitude(Double.parseDouble(item2.getFirstChild().getNodeValue()));
                }
                if (name.equalsIgnoreCase("course")) {
                    pt.setBearing(Float.parseFloat(item2.getFirstChild().getNodeValue()));
                }
                if (name.equalsIgnoreCase("speed")) {
                    pt.setSpeed(Float.parseFloat(item2.getFirstChild().getNodeValue()));
                }
                if (name.equalsIgnoreCase("hdop")) {
                    pt.setAccuracy(Float.parseFloat(item2.getFirstChild().getNodeValue()) * 5);
                }
                if (name.equalsIgnoreCase("time")) {
                    pt.setTime((getDateFormatter().parse(item2.getFirstChild().getNodeValue())).getTime());
                }

            }

            for (int y = 0; y < props.getLength(); y++) {
                Node item3 = props.item(y);
                String name = item3.getNodeName();
                if (!name.equalsIgnoreCase("ele")) {
                    continue;
                }
                pt.setAltitude(Double.parseDouble(item3.getFirstChild().getNodeValue()));
            }

            points.add(new SerializableLocation(pt));

        }

        fis.close();

        return points;
    }
}