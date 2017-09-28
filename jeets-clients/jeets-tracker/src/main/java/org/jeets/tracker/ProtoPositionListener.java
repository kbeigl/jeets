package org.jeets.tracker;

import org.jeets.protocol.Traccar;

public interface ProtoPositionListener {
    
    void transmitPositionProto(Traccar.Position.Builder positionProto);

//    void transmitPositionEntity(Position positionEntity);

}
