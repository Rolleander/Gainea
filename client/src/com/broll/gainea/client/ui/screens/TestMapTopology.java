package com.broll.gainea.client.ui.screens;

import com.broll.gainea.net.proto.NT_Expansion;
import com.broll.gainea.net.proto.NT_Location;
import com.broll.gainea.net.proto.NT_LocationContainer;
import java.util.Collections;
import java.util.List;

/** Small protocol fixture used by the offline UI preview screens. */
final class TestMapTopology {
    private TestMapTopology() {}

    static List<NT_Expansion> create() {
        String[] names = {"Grünland", "Felswald", "Große Wüste", "Xom-Delta", "Küstengebiet", "Weidesteppe", "Testgebiet"};
        NT_LocationContainer.Builder locations = NT_LocationContainer.newBuilder().setName("Testkarte");
        for (int i = 0; i < names.length; i++) {
            locations.addLocations(NT_Location.newBuilder().setNumber(i).setName(names[i])
                .setX(-500f + (i % 4) * 350f).setY(-250f + (i / 4) * 400f).setAreaType(i % 5));
        }
        return Collections.singletonList(NT_Expansion.newBuilder().setName("Testkarte")
            .setTexture("expansion_0.png").setX(0f).setY(0f).addLocationContainers(locations).build());
    }
}
