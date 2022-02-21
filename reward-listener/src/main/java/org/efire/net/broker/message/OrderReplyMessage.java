package org.efire.net.broker.message;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode
public class OrderReplyMessage {
    private String replyMessage;
}
