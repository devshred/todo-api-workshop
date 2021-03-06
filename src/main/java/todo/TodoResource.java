package todo;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/api/v1/todo")
public class TodoResource {
  @Inject TodoService service;

  @GET
  public List<TodoEntity> allTodoItems() {
    return service.all();
  }

  @POST
  public Response createTodoItem(TodoEntity todoEntity) {
    return Response.status(201).entity(service.create(todoEntity)).build();
  }

  @GET
  @Path("/{id}")
  public TodoEntity getTodoItem(@PathParam("id") UUID id) {
    TodoEntity todoEntity = service.findById(id);

    if (todoEntity == null) {
      throw new WebApplicationException("TodoItem with " + id + " does not exist.", 404);
    }

    return todoEntity;
  }

  @PATCH
  @Path("/{id}")
  public Response changeTodoItem(@PathParam("id") UUID id, TodoEntity patchedObject) {
    if (service.changeStatus(id, patchedObject.done)) {
      return Response.noContent().build();
    } else {
      throw new WebApplicationException("TodoItem with " + id + " does not exist.", 404);
    }
  }

  @DELETE
  @Path("/{id}")
  public Response deleteTodoItem(@PathParam("id") UUID id) {
    if (service.delete(id)) {
      return Response.noContent().build();
    } else {
      throw new WebApplicationException("TodoItem with " + id + " does not exist.", 404);
    }
  }
}
