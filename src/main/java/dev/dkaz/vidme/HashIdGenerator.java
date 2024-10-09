package dev.dkaz.vidme;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HashIdGenerator {
    private final Hashids hashids;

    public HashIdGenerator(@Value("${vidme.hashid.salt}") String salt, @Value("${vidme.hashid.minlength}") int minLength) {
        hashids = new Hashids(salt, minLength);
    }

    public String encode(Long id) {
        return hashids.encode(id);
    }

    public Long decode(String hash) {
        return hashids.decode(hash)[0];
    }
}
