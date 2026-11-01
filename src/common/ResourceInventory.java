
package common;

import java.util.HashMap;
import java.util.Map;

public class ResourceInventory {
    private final Map<String, Integer> quantities = new HashMap<>();

    public ResourceInventory() {
        for (String type : ResourceNames.ALL) quantities.put(type, 0);
    }

    public boolean hasResourceType(String type) {
        return quantities.containsKey(type);
    }

    public int get(String type) {
        if (!hasResourceType(type)) return -1;
        return quantities.get(type);
    }

    public boolean add(String type, int amount) {
        if (!hasResourceType(type) || amount < 0) return false;
        int currentAmount = get(type);
        if (amount > Integer.MAX_VALUE - currentAmount) return false;
        quantities.put(type, currentAmount + amount);
        return true;
    }

    public boolean remove(String type, int amount) {
        if (!hasResourceType(type) || amount < 0 || get(type) < amount) return false;
        quantities.put(type, get(type) - amount);
        return true;
    }

    public String summary() {
        StringBuilder result = new StringBuilder();
        for (String type : ResourceNames.ALL) {
            result.append(type).append("=").append(get(type)).append(" ");
        }
        return result.toString().trim();
    }
}
