package demo.mikko;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/index")
public class IndexResource {
    
    @Inject
    IndexerService indexerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public IndexResult startIndex() {
        try {
            return indexerService.indexFiles();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
