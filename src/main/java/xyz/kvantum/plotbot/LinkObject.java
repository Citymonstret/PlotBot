package xyz.kvantum.plotbot;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import xyz.kvantum.plotbot.configuration.serialization.ConfigurationSerializable;

@Getter
@RequiredArgsConstructor
public final class LinkObject implements ConfigurationSerializable {

  private final String name;
  private final String link;
  private final String desc;

  @Override
  public Map<String, Object> serialize() {
    final Map<String, Object> map = new HashMap<>();
    map.put("link", this.getLink());
    map.put("desc", this.getDesc());
    map.put("name", this.getName());
    return map;
  }

  public static LinkObject deserialize(@NonNull final Map<String, Object> map) {
    final String link = map.get("link").toString();
    final String desc = map.get("desc").toString();
    final String name = map.get("name").toString();
    return new LinkObject(name, link, desc);
  }

}
