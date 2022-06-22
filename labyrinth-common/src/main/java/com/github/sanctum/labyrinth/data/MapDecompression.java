package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;
import java.util.Map;
import java.util.Set;

public interface MapDecompression {

	Set<String> toSet();

	LabyrinthCollection<String> toLabyrinthSet();

	Map<String, Object> toMap();

	LabyrinthMap<String, Object> toLabyrinthMap();

}
