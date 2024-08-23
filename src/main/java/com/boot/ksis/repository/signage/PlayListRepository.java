package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    List<PlayList> findByDevice(Device device);

    PlayList findByPlaylistId(Long playlistId);
}
