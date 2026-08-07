package cultureland.hackathon.team.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class InviteCodeGenerator {

    // 헷갈리는 문자 제외 (0/O, 1/I)
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i=0; i<CODE_LENGTH; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }

}
