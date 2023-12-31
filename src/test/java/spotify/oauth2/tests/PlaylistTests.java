package spotify.oauth2.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import spotify.oauth2.api.applicationAPI.PlaylistAPI;
import spotify.oauth2.pojo.Error;
import spotify.oauth2.pojo.Playlist;
import spotify.oauth2.utils.ConfigLoader;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Automate Spotify API with OAuth 2.0")
@Feature("Spotify Playlist API")
public class PlaylistTests {
    String playlistId = ConfigLoader.getInstance().getPlaylistId();

    @Story("Automate Positive Testcase of Playlist API")
    @Description("This is the create playlist API")
    @Test(description = "Create Playlist")
    public void createPlaylist() {
        Playlist requestPlaylist = playlistBuilder("New Playlist", "New playlist description", false);
        Response response = PlaylistAPI.post(requestPlaylist);
        assertStatusCode(response.statusCode(), 201);
        Playlist responsePlaylist = response.as(Playlist.class);
        assertPlaylist(responsePlaylist, requestPlaylist);
    }
    @Story("Automate Positive Testcase of Playlist API")
    @Description("This is the get playlist API")
    @Test(description = "Get Playlist")
    public void getPlaylist() {
        Playlist requestPlaylist = playlistBuilder("Updated Playlist Name", "Updated playlist description", false);
        Response response = PlaylistAPI.get(playlistId);
        assertStatusCode(response.statusCode(), 200);
        Playlist responsePlaylist = response.as(Playlist.class);
        assertPlaylist(responsePlaylist, requestPlaylist);
    }

    @Story("Automate Positive Testcase of Playlist API")
    @Story("Automate Playlist API")
    @Description("This is the update playlist API")
    @Test(description = "Update Playlist")
    public void updatePlaylist() {
        Playlist requestPlaylist = playlistBuilder("Updated Playlist Name", "Updated playlist description", false);
        Response response = PlaylistAPI.update(playlistId, requestPlaylist);
        assertStatusCode(response.statusCode(), 200);

    }

    @Story("Automate Negative Testcase of Playlist API")
    @Description("This is the negative test with blank name of playlist in create API")
    @Test(description = "Negative test with blank name of Playlist")
    public void createPlaylistWithBlankName() {
        Playlist requestPlaylist = playlistBuilder("", "New playlist description", false);
        Response response = PlaylistAPI.post(requestPlaylist);
        assertStatusCode(response.statusCode(), 400);
        Error error = response.as(Error.class);
        assertError(error, 400, "Missing required field: name");
    }

    @Story("Automate Negative Testcase of Playlist API")
    @Description("This is the negative test with invalid access token name in create API")
    @Test(description = "Negative Test with invalid access token")
    public void createPlaylistWithExpiredToken() {
        String inValidAccessToken = "12345";
        Playlist requestPlaylist = playlistBuilder("New Playlist", "New playlist description", false);
        Response response = PlaylistAPI.post(inValidAccessToken, requestPlaylist);
        assertStatusCode(response.statusCode(), 401);
        Error error = response.as(Error.class);
        assertError(error, 401, "Invalid access token");
    }

    @Step
    public Playlist playlistBuilder(String name, String description, boolean _public){
        return Playlist.builder().
                name(name).
                description(description).
                _public(_public).build();
    }

    @Step
    public void assertPlaylist(Playlist responsePlaylist, Playlist requestPlaylist) {
        assertThat(responsePlaylist.getName(), equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(), equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.get_public(), equalTo(requestPlaylist.get_public()));
    }

    @Step
    public void assertStatusCode(int actualStatusCode, int expectedStatusCOde) {
        assertThat(actualStatusCode, equalTo(expectedStatusCOde));
    }

    @Step
    public void assertError(Error error, int expectedStatusCode, String expectedErrorMsg) {
        assertThat(error.getError().getStatus(), equalTo(expectedStatusCode));
        assertThat(error.getError().getMessage(), equalTo(expectedErrorMsg));
    }
}
