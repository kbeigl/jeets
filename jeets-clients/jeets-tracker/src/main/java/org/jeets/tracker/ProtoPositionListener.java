package org.jeets.tracker;

import org.jeets.protocol.Traccar;

public interface ProtoPositionListener {
    
    void addPositionProto(Traccar.Position.Builder positionProto);

//    void transmitPositionEntity(Position positionEntity);

}
