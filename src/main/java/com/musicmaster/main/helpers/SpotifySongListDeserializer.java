package com.musicmaster.main.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.musicmaster.main.models.SpotifySong;

import java.io.IOException;
import java.util.List;

public class SpotifySongListDeserializer extends StdDeserializer<List<SpotifySong>> {

    private static ObjectMapper objectMapper = new ObjectMapper();
    public SpotifySongListDeserializer() {this(null);}
    public SpotifySongListDeserializer(Class<?> origin) {
        super(origin);
        objectMapper = new ObjectMapper();
    }
    @Override
    public List<SpotifySong> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        TreeNode treeNode = objectMapper.readTree(jsonParser);
        String txt = jsonParser.readValueAsTree().toString();
//        String txt2 = jsonParser.getText();
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode  songsNode = rootNode.get("items");

        CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, SpotifySong.class);
        List<SpotifySong> songs = objectMapper.readerFor(collectionType).readValue(rootNode);
        return songs;
    }
}
