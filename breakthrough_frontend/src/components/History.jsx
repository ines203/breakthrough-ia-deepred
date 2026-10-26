
import '../gameInterface.css';
import HistoryMove from './HistoryMove';

const History = ({ currentIndex, playedMove, onClickHistoryMove }) => {
    const len = playedMove.length;
    return (
        <div className='history-container'>
            {playedMove.slice().reverse().map(((move, index) => {
                const realIndex = len - index;
                const isActive = realIndex === currentIndex;
                return (<HistoryMove
                    key={index}
                    index={realIndex}
                    order={realIndex}
                    move={move}
                    piece_color={realIndex % 2 === 1 ? 1 : 2}
                    isActive={isActive}
                    onClickHistoryMove={onClickHistoryMove}
                />)

            }))}
        </div>
    );
};

export default History;
