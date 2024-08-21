package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.IdClass.PlaylistSequenceId;
import com.boot.ksis.entity.MapsId.PlaylistSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSequenceRepository extends JpaRepository<PlaylistSequence, PlaylistSequenceId> {
    List<PlaylistSequence> findByPlaylistId(Long playlistId);

    void deleteByPlaylistId(Long playlistId);
}