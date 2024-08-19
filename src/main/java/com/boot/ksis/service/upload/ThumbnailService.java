package com.boot.ksis.service.upload;

import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import com.boot.ksis.repository.upload.ThumbNailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ThumbNailRepository thumbNailRepository;
    private final OriginalResourceRepository originalResourceRepository;

    public void saveThumbnail(ThumbNail thumbnail){
        thumbNailRepository.save(thumbnail);
    }


}
