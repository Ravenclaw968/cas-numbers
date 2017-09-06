import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.HashMap;

import java.lang.Thread;

public class Driver
{
    public static HtmlPage page;
    public static ExcelReader reader;
    public static void main(String[] args) throws Exception
    {
        reader = new ExcelReader();
        page = new WebClient().getPage("http://www.organic-chemistry.org/chemicals/search.htm");
        HashMap<String, String> compounds = ExcelReader.getCompounds();
        int done = 0;
        for (String compound : compounds.keySet())
        {
            while (true)
            {
                try
                {
                    getCAS(compound, compounds.get(compound));
                    done ++;
                    System.out.println(done + " Done");
                    Thread.sleep(3000);
                    break;
                }
                catch (Exception e)
                {

                }
            }
        }
        reader.save();
    }
    public static void getCAS(String compound, String cell) throws Exception
    {
        HtmlForm form = page.getFormByName("form");
        form.getInputByName("searchValue").setValueAttribute(compound);
        HtmlPage nextPage = (HtmlPage) form.getInputByValue("Search").click();
        WebResponse response = nextPage.getWebResponse();
        String content = response.getContentAsString();
        String searchDelimeter = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
        int position = content.indexOf(searchDelimeter);
        if (position == -1)
        {
            reader.writeToCell("C" + cell.substring(1, cell.length()), compound);
        }
        else
        {
            String[] split = content.split(searchDelimeter);
            if (split.length > 11)
            {
                String[] items = new String[11];
                for (int i = 0; i < 11; i ++)
                {
                    items[i] = split[i];
                }
                split = items;
            }
            String bestCAS = "";
            int minimumEditDistance = Integer.MAX_VALUE;
            String bestName = "";
            for (int i = 1; i < split.length; i ++)
            {
                if (split[i].indexOf("<td nowrap>") != -1)
                {
                    int nowrap_position = split[i].indexOf("<td nowrap>") + 11;
                    String cas = "";
                    for (int j = nowrap_position; j < split[i].length(); j ++)
                    {
                        if (split[i].substring(j, j + 1).equals("<"))
                        {
                            break;
                        }
                        else
                        {
                            cas += split[i].substring(j, j + 1);
                        }
                    }
                    String previous = split[i - 1];
                    String[] split2 = previous.split("<tr><td>");
                    int min_edit_distance = Integer.MAX_VALUE;
                    String bestMan = "";
                    for (String s : split2)
                    {
                        String build = "";
                        for (int j = 0; j < s.length(); j ++)
                        {
                            if (s.substring(j, j + 1).equals("<"))
                            {
                                break;
                            }
                            else
                            {
                                build += s.substring(j, j + 1);
                            }
                        }
                        if (! build.replaceAll("\\s+", "").equals(""))
                        {
                            int edit_distance = editDistance(compound, build);
                            if (edit_distance < min_edit_distance)
                            {
                                min_edit_distance = edit_distance;
                                bestMan = build;
                            }
                        }
                    }
                    if (min_edit_distance < minimumEditDistance)
                    {
                        minimumEditDistance = min_edit_distance;
                        bestCAS = cas;
                        bestName = bestMan;
                    }
                }
            }
            System.out.println(bestCAS);
            System.out.println(bestName);
            reader.writeToCell("C" + cell.substring(1, cell.length()), bestCAS);
            reader.writeToCell("D" + cell.substring(1, cell.length()), bestName);
        }
    }
    public static int editDistance(String first, String second)
    {
        int[][] distance = new int[first.length() + 1][second.length() + 1];
        distance[0][0] = 0;
        for (int i = 1; i <= first.length(); i ++)
        {
            distance[i][0] = distance[i - 1][0] + 1;
        }
        for (int i = 1; i <= second.length(); i ++)
        {
            distance[0][i] = distance[0][i - 1] + 1;
        }
        for (int i = 1; i <= first.length(); i ++)
        {
            for (int j = 1; j <= second.length(); j ++)
            {
                if (first.substring(i - 1, i).equals(second.substring(j - 1, j)))
                {
                    distance[i][j] = distance[i - 1][j - 1];
                }
                else
                {
                    int value = Math.min(distance[i - 1][j], distance[i][j - 1]);
                    distance[i][j] = value + 1;
                }
            }
        }
        return distance[distance.length - 1][distance[0].length - 1];
    }
}
