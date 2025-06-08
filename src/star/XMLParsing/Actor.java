package star.XMLParsing;

import java.util.HashMap;
import java.util.Objects;

public class Actor
{
    public String name = "";


    //sort of like the primary key of a movie. used to identify the movie;
    //so this saves all the movies that the actor with name X has played in
    public HashMap<String,String> titles_directors = new HashMap<>();
                //<title,director>
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Actor)) return false;
        Actor actor = (Actor) o;
        return name.equals(actor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Actor()
    {}

    @Override
    public String toString() {
        return name;
    }
}
