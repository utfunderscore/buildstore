package org.readutf.buildstore.server.utils;



import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PlayerFetcher {

    private static @NonNull final Gson gson = new Gson();
    private static @NonNull final String api = "https://api.minetools.eu/uuid/%s";

    private static Map<String, Object> getPlayerJson(UUID uuid, String name) throws IOException {
        URL url = URI.create(String.format(api, uuid == null ? name : uuid.toString())).toURL();
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        return gson.fromJson(bufferedReader, new TypeToken<>() {});
    }

    public static UUID getUUID(String name) throws IOException {
        String uuidTemp = (String) getPlayerJson(null, name).get("id");
        String uuid = "";
        for (int i = 0; i <= 31; i++) {
            uuid = uuid + uuidTemp.charAt(i);
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                uuid = uuid + "-";
            }
        }

        if(getPlayerJson(null, name) != null){
            return UUID.fromString(uuid);
        }
        return null;
    }

    public static @NonNull Optional<String> getName(UUID uuid) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if(offlinePlayer.getName() != null && !offlinePlayer.getName().equalsIgnoreCase("")) {
            return Optional.of(offlinePlayer.getName());
        }

        try {
            if(getPlayerJson(uuid, null) != null){
                return Optional.ofNullable((String) getPlayerJson(uuid, null).get("name"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}