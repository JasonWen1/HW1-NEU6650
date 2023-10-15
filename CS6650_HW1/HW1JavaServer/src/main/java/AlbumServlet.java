import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

@WebServlet(name = "AlbumServlet", value = "/albums/*")
@MultipartConfig()
public class AlbumServlet extends HttpServlet {
  //album id is key, and album other contents are value
  static Map<Integer, Album> albumMap = new HashMap<>();
  static class Album {
    public byte[] imageContent;
    Profile albumInfo;
  }

  static class Profile {
    String artist;
    String title;
    String year;

  }

  @Override
  //Get album by key
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      res.setContentType("application/json");
      String urlPath = req.getPathInfo();
      if (urlPath == null || urlPath.isEmpty()) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getWriter().write(new Gson().toJson("URL missing parameter."));
        return;
      }

      String[] urlParts = urlPath.split("/");

      if(!isUrlValid(urlParts)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getWriter().write(new Gson().toJson("URL format is incorrect."));
        return;
      } else {
        int albumId = Integer.parseInt(urlParts[1]);
        Album album = albumMap.get(albumId);

        if (album == null) {
          res.setStatus(HttpServletResponse.SC_NOT_FOUND);
          res.getWriter().write(new Gson().toJson("Album not found."));
          return;
        }
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(new Gson().toJson(album.albumInfo));
      }
    } catch (Exception e) {
      res.getWriter().write(e.getMessage());
    }
  }

  @Override
  //Returns the new key and size of an image in bytes
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      res.setContentType("application/json");

      //check multipart
      if (!ServletFileUpload.isMultipartContent(req)) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().write(new Gson().toJson("Request is not multipart."));
        return;
      }
      String urlPath = req.getPathInfo();
      ServletFileUpload upload = new ServletFileUpload();
      FileItemIterator iter = upload.getItemIterator(req);
      Album album1 = new Album();
      Profile profile = new Profile();

      while(iter.hasNext()) {
        FileItemStream item = iter.next();
        String name = item.getFieldName();
        InputStream stream = item.openStream();
        if (!item.isFormField()) {
          if ("image".equals(name)) {
            album1.imageContent = IOUtils.toByteArray(stream);  // Apache Commons IO
          }
        } else {
          if ("profile".equals(name)) {
            String albumInfoJson = IOUtils.toString(stream, String.valueOf(StandardCharsets.UTF_8));
            profile = new Gson().fromJson(albumInfoJson, Profile.class);
          }
        }
      }
      album1.albumInfo = profile;
      int id1 = albumMap.size() + 1;
      albumMap.put(id1, album1);
      Map<String, Object> responseData = new HashMap<>();
      responseData.put("albumId", id1);
      responseData.put("imageSize", album1.imageContent.length);
      String responseMessage = new Gson().toJson(responseData);
      res.setStatus(HttpServletResponse.SC_OK);
      res.getWriter().write(responseMessage);

    } catch (Exception e) {
      res.getWriter().write(e.getMessage());
    }
  }

  //Check url length and format
  boolean isUrlValid(String[] urlPath) {
    if (urlPath.length != 2) {
      return false;
    }
    String str = urlPath[1];
    for (int i = 0; i < str.length(); i++) {
      System.out.println(str.charAt(i));
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}