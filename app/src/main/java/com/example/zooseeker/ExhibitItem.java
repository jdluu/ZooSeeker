package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "exhibit_list_items") public class ExhibitItem {
    @PrimaryKey @NonNull public String          id;
    @NonNull public             VertexInfo.Kind kind;
    @NonNull public             String          name;
    @NonNull public             String          tags;
    public                      boolean         added;

    public ExhibitItem(@NonNull String id, @NonNull VertexInfo.Kind kind,
                       @NonNull String name, @NonNull String tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.added = false;
    }

    @NonNull @Override public String toString() {
        return "ExhibitListItem{" + "id=" + id + ", kind=" + kind + ", name=" +
               name + "}, tags=[" + tags + "], added=" + added + "}";
    }

    public static List<ExhibitItem> loadJSON(Context context, String path) {
        List<VertexInfo> vertexInfos =
                VertexInfo.loadVertexInfoJSON(context, path);
        for (VertexInfo item : vertexInfos) {
            if (item.kind == VertexInfo.Kind.EXHIBIT) {
                ExhibitItem exhibitItem =
                        new ExhibitItem(item.id, item.kind, item.name,
                                        String.join(", ", item.tags));
                ExhibitList.allExhibits.add(exhibitItem);
            }
        }
        return ExhibitList.allExhibits;
    }
}
