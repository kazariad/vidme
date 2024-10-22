package dev.dkaz.vidme.video;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

public class VideoRepositoryExtImpl implements VideoRepositoryExt {
    private final JdbcClient jdbcClient;
    private final VideoRepository videoRepository;
    // re-use Spring Data JDBC's generated Entity mapper instead of a manual RowMapper implementation
    private final EntityRowMapper<Video> entityRowMapper;

    public VideoRepositoryExtImpl(
            JdbcClient jdbcClient,
            JdbcMappingContext jdbcMappingContext,
            JdbcConverter jdbcConverter,
            // use Lazy proxy to avoid cyclical dependency errors
            @Lazy VideoRepository videoRepository
    ) {
        this.jdbcClient = jdbcClient;
        this.videoRepository = videoRepository;
        RelationalPersistentEntity<Video> rpe = (RelationalPersistentEntity<Video>) jdbcMappingContext.getPersistentEntity(Video.class);
        this.entityRowMapper = new EntityRowMapper<>(rpe, jdbcConverter);
    }

    @Override
    public List<Video> findFeatured(int page, int pageSize) {
        String sql = "SELECT * FROM `video` ORDER BY RAND() LIMIT ? OFFSET ?";
        return jdbcClient.sql(sql)
                .param(pageSize)
                .param(pageSize * page)
                .query(entityRowMapper).list();
    }

    @Override
    public List<Video> findByTitleDescriptionSearch(String query, VideoSort sort, int page, int pageSize) {
        if (query.isBlank() || query.equals("*")) {
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            pageRequest = switch (sort) {
                case NEWEST -> pageRequest.withSort(Sort.sort(Video.class).by(Video::getCreatedAt).descending());
                case OLDEST -> pageRequest.withSort(Sort.sort(Video.class).by(Video::getCreatedAt).ascending());
                case VIEWS -> pageRequest.withSort(Sort.sort(Video.class).by(Video::getViews).descending());
            };
            return videoRepository.findAll(pageRequest).toList();
        }

        String sql = "SELECT * FROM `video` WHERE MATCH(title, description) AGAINST(?)";
        sql += switch (sort) {
            case NEWEST -> " ORDER BY `created_at` DESC";
            case OLDEST -> " ORDER BY `created_at` ASC";
            case VIEWS -> " ORDER BY `views` DESC";
        };
        sql += " LIMIT ? OFFSET ?";

        return jdbcClient.sql(sql)
                .param(query)
                .param(pageSize)
                .param(pageSize * page)
                .query(entityRowMapper).list();
    }
}
