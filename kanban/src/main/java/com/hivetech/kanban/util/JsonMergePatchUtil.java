package com.hivetech.kanban.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMergePatchUtil {

    private JsonMergePatchUtil() {}

    public static <T> T merge(
            ObjectMapper objectMapper,
            T target,
            JsonNode patchNode,
            Class<T> clazz
    ) {
        JsonNode targetNode = objectMapper.valueToTree(target);
        JsonNode mergedNode = mergeNodes(targetNode, patchNode);
        return objectMapper.convertValue(mergedNode, clazz);
    }

    private static JsonNode mergeNodes(JsonNode target, JsonNode patch) {
        if (patch.isNull()) {
            return patch;
        }

        if (patch.isObject()) {
            patch.fieldNames().forEachRemaining(fieldName -> {
                JsonNode patchValue = patch.get(fieldName);
                JsonNode targetValue = target.get(fieldName);

                if (patchValue.isNull()) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) target)
                            .remove(fieldName);
                } else if (targetValue != null && targetValue.isObject()) {
                    mergeNodes(targetValue, patchValue);
                } else {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) target)
                            .set(fieldName, patchValue);
                }
            });
        }
        return target;
    }
}