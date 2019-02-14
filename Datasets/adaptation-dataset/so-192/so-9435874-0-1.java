public class foo {
public static List<Location> getPoints(File gpxFile)
{
    List<Location> points = null;
    try
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        FileInputStream fis = new FileInputStream(gpxFile);
        Document dom = builder.parse(fis);
        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("trkpt");

        points = new ArrayList<Location>();

        for(int j = 0; j < items.getLength(); j++)
        {
            Node item = items.item(j);
            NamedNodeMap attrs = item.getAttributes();
            NodeList props = item.getChildNodes();

            Location pt = new Location("test");

            pt.setLatitude(Double.parseDouble(attrs.getNamedItem("lat").getTextContent()));
            pt.setLongitude(Double.parseDouble(attrs.getNamedItem("lon").getTextContent()));

            for(int k = 0; k<props.getLength(); k++)
            {
                Node item2 = props.item(k);
                String name = item2.getNodeName();
                if(!name.equalsIgnoreCase("time")) continue;
                try
                {
                    pt.setTime((getDateFormatter().parse(item2.getFirstChild().getNodeValue())).getTime());
                }

                catch(ParseException ex)
                {
                    ex.printStackTrace();
                }
            }

            for(int y = 0; y<props.getLength(); y++)
            {
                Node item3 = props.item(y);
                String name = item3.getNodeName();
                if(!name.equalsIgnoreCase("ele")) continue;
                pt.setAltitude(Double.parseDouble(item3.getFirstChild().getNodeValue()));
            }

            points.add(pt);

        }

        fis.close();
    }

    catch(FileNotFoundException e)
    {
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    catch(ParserConfigurationException ex)
    {

    }

    catch (SAXException ex) {
    }

    return points;
}
}