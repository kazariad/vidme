package dev.dkaz.vidme.video;


import java.util.List;

interface VideoRepositoryExt {
    List<Video> findFeatured(int page, int pageSize);

    List<Video> findByTitleDescriptionSearch(String query, VideoSort sort, int page, int pageSize);
}
