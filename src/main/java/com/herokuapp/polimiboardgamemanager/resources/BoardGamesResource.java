package com.herokuapp.polimiboardgamemanager.resources;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;

// Will map the resource to the URL boardgames
@Path("/boardgames")
public class BoardGamesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Return the list of board games for applications
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<BoardGame> getBoards() {
        return BoardGameDao.getInstance().getAllBoardGames();
    }
    
    // Return the list of board games to the user in the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public List<BoardGame> getBoardsBrowser() {
        return BoardGameDao.getInstance().getAllBoardGames();
    }    

    // returns the number of board games
    @GET
    @Path("count")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public long getCount() {
        return BoardGameDao.getInstance().getBoardGamesCount();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newBoardGameForm(@FormParam("name") String name,
                                 @FormParam("designers") String designers,
                                 @FormParam("cover") String cover) throws IOException {
        
        BoardGameDao.getInstance().insertBoardGame(name, designers, cover);
        //servletResponse.sendRedirect("../create_boardgame.html");
    }
    
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public void newBoardGameJson(BoardGame board,
                        @Context HttpServletResponse servletResponse) throws IOException {
        
        BoardGameDao.getInstance().insertBoardGame(board);

        //servletResponse.sendRedirect("../create_boardgame.html");
    }
    

    @Path("{board_id}")
    public BoardGameResource getBoard(@PathParam("board_id") int id) {
        return new BoardGameResource(uriInfo, request, id);
    }

}