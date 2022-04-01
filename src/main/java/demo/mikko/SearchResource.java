package demo.mikko;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.lucene.queryparser.classic.ParseException;

@Path("/query")
public class SearchResource {
    
    @Inject
    SearchService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult query(@QueryParam("q") String query, 
                    @QueryParam("max") @DefaultValue("200") int max,
                    @QueryParam("offset") @DefaultValue("0") int offset) {
        try {
            return service.doQuery(query, max, offset);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
