import '../gameInterface.css';
import { FaChessPawn } from "react-icons/fa";

const HistoryMove = ({ index, order, move, piece_color, isActive, onClickHistoryMove }) => {
    return (

        <div className={`history-item ${isActive ? 'history-move-active' : ''}`} onClick={() => onClickHistoryMove(index)}>
            <div className='history-icon'>
                <FaChessPawn className={`piece piece-${piece_color}`} />
            </div>
            <div className='history-info'>
                <p className='history-move'>
                    {order}. {piece_color === 1 ? "Blanc" : "Noir"} {move.source.x}:{move.source.y} → {move.target.x}:{move.target.y}
                </p>
            </div>
        </div>

    );
};

export default HistoryMove;