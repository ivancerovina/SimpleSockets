package me.ivancerovina.simplesockets.packet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.reflections.Reflections;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

public class PacketManager {
    private HashMap<String, Class<? extends Packet>> packets = new HashMap<>();

    public void registerPacket(Class<? extends Packet> packet) {
        packets.put(packet.getSimpleName(), packet);
    }

    public void registerPacketsInPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends Packet>> subTypes = reflections.getSubTypesOf(Packet.class);

        for (Class<? extends Packet> clazz : subTypes) {
            registerPacket(clazz);
        }
    }

    public void unregisterPacket(Class<? extends Packet> packet) {
        packets.remove(packet.getSimpleName());
    }

    public byte[] serializePacket(Packet packet) throws JsonProcessingException {
        if (!packets.containsKey(packet.getClass().getSimpleName())) {
            throw new IllegalArgumentException("Packet " + packet.getClass().getSimpleName() + " is not registered");
        }

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", packet.getClass().getSimpleName());
        rootNode.setAll((ObjectNode) mapper.valueToTree(packet));

        String json = mapper.writeValueAsString(rootNode);

        return PacketProtocol.wrapMessage(json.getBytes(StandardCharsets.UTF_8));
    }

    public Packet deserializePacket(byte[] data) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var json = new String(data, StandardCharsets.UTF_8);

        JsonNode rootNode = mapper.readTree(json);
        JsonNode typeNode = rootNode.get("type");

        if (typeNode == null || !typeNode.isTextual()) {
            throw new IllegalArgumentException("Missing or invalid 'type' field");
        }

        String type = typeNode.asText();
        Class<? extends Packet> packetClass = packets.get(type);

        if (packetClass == null) {
            throw new IllegalArgumentException("Unknown or unregistered packet type: " + type);
        }

        ((ObjectNode) rootNode).remove("type");

        return mapper.treeToValue(rootNode, packetClass);
    }
}
