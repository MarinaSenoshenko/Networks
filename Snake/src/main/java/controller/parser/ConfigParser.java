package controller.parser;

import exceptions.EditConfigException;
import proto.SnakesProto.GameConfig;

import java.util.Properties;

import static controller.parser.ConfigConstants.*;

public class ConfigParser {

    public static GameConfig createNewConfig(Properties properties) {
        int width, height, food_per_player, food_static,
                state_delay_ms, ping_delay_ms, node_timeout_ms;

        try {
            width = Integer.parseInt(properties.getProperty("width"));
            if (width < MIN_SIDE || width > MAX_SIDE) {
                throw new EditConfigException();
            }
        } catch (NumberFormatException e) {
            width = NORMAL_SIDE;
        }
        try {
            height = Integer.parseInt(properties.getProperty("height"));
            if (height < MAX_SIDE || height > MAX_SIDE) {
                throw new EditConfigException();
            }
        } catch (NumberFormatException e) {
            height = NORMAL_SIDE;
        }
        try {
            food_per_player = Integer.parseInt(properties.getProperty("food_per_player"));
            if (food_per_player < 0) {
                throw new EditConfigException();
            }
        }
        catch (NumberFormatException e) {
            food_per_player = NORMAL_FOOD_PER_PLAYER;
        }
        try {
            food_static = Integer.parseInt(properties.getProperty("food_static"));
            if (food_static < 0) {
                throw new EditConfigException();
            }
         }
        catch (NumberFormatException e) {
            food_static = NORMAL_FOOD_STATIC;
        }
        try {
            state_delay_ms = Integer.parseInt(properties.getProperty("state_delay_ms"));
            if (state_delay_ms < 0) {
                throw new EditConfigException();
            }
        }
        catch (NumberFormatException e) {
            state_delay_ms = NORMAL_STATE_DELAY_MS;
        }
        try {
            ping_delay_ms = Integer.parseInt(properties.getProperty("ping_delay_ms"));
            if (ping_delay_ms < 0) {
                throw new EditConfigException();
            }
        }
        catch (NumberFormatException e) {
            ping_delay_ms = NORMAL_PING_DELAY_MS;
        }
        try {
            node_timeout_ms = Integer.parseInt(properties.getProperty("node_timeout_ms"));
            if (node_timeout_ms < 0) {
                throw new EditConfigException();
            }
        }
        catch (NumberFormatException e) {
            node_timeout_ms = NORMAL_NODE_TIMEOUT_MS;
        }

        return GameConfig.newBuilder()
                .setWidth(width)
                .setHeight(height)
                .setFoodPerPlayer(food_per_player)
                .setFoodStatic(food_static)
                .setStateDelayMs(state_delay_ms)
                .setPingDelayMs(ping_delay_ms)
                .setNodeTimeoutMs(node_timeout_ms)
                .build();
    }
}
