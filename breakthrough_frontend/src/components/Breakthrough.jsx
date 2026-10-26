import { useState, useEffect } from "react";
import { Set, Map } from 'immutable';
import api from "../api/axiosConfig";
import '../gameInterface.css';
import Board from './Board';
import History from "./History";

const Breakthrough = () => {
    const [gameStatus, setGameStatus] = useState({ code: 0, description: "Non initialisé" });
    const [legalMoves, setLegalMoves] = useState([]);
    const [highlightedSquare, setHighlightedSquare] = useState(new Set());
    const [board, setBoard] = useState([
        [0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 1, 2, 0, 0, 0],
        [0, 0, 0, 2, 1, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0],
    ]);
    const [readyPlayerIds, setReadyPlayerIds] = useState([]);
    const [selectedWhitePlayerId, setSelectedWhitePlayerId] = useState("");
    const [selectedBlackPlayerId, setSelectedBlackPlayerId] = useState("");
    const [moveTimeout, setMoveTimeout] = useState(3000);
    const [playedMove, setPlayedMoved] = useState([]);
    const [isReplayMode, setIsReplayMode] = useState(false);
    const [currentReplayIndex, setcurrentReplayIndex] = useState(0);

    useEffect(() => {
        // Aller chercher la liste des joueurs prêts
        const fetchReadyPlayerIds = async () => {
            const rPlayersReady = await api.get("/board/readyplayerids");
            setReadyPlayerIds(rPlayersReady.data);
            setSelectedWhitePlayerId(rPlayersReady.data[0]);
            setSelectedBlackPlayerId(rPlayersReady.data[0]);
        }

        fetchReadyPlayerIds();

        // Subscribe aux évènements
        const baseSubscribeUrl = "http://127.0.0.1:8080/board/subscribe"

        const boardEventSource = new EventSource(baseSubscribeUrl + "/board");
        const legalMovesEventSource = new EventSource(baseSubscribeUrl + "/legalmoves");
        const gameStatusEventSource = new EventSource(baseSubscribeUrl + "/gamestatus");
        const playersReadyEventSource = new EventSource(baseSubscribeUrl + "/readyplayerids");
        const playedMoveEventSource = new EventSource(baseSubscribeUrl + "/playedmoves");

        playedMoveEventSource.onmessage = (event) => {
            const moves = JSON.parse(event.data);
            setPlayedMoved(moves);
            setcurrentReplayIndex(moves.length);
        }

        boardEventSource.onmessage = (event) => {
            const board = JSON.parse(event.data);
            setBoard(board);
        }

        legalMovesEventSource.onmessage = (event) => {
            const legalMoves = JSON.parse(event.data);
            setLegalMoves(legalMoves);
        }

        gameStatusEventSource.onmessage = (event) => {
            const gameStatus = JSON.parse(event.data);
            setGameStatus(gameStatus);
        }

        playersReadyEventSource.onmessage = (event) => {
            const playersReady = JSON.parse(event.data);
            setReadyPlayerIds(playersReady);
            setSelectedWhitePlayerId(playersReady[0]);
            setSelectedBlackPlayerId(playersReady[0]);
        }

        // terminating the connection on component unmount
        return () => {
            boardEventSource.close();
            legalMovesEventSource.close();
            gameStatusEventSource.close();
            playersReadyEventSource.close();
            playedMoveEventSource.close();
        };
    }, []);

    async function startGame() {
        try {
            goToReplayIndex(playedMove.length)
            await api.post("/board/newgame", {
                whitePlayerId: selectedWhitePlayerId,
                blackPlayerId: selectedBlackPlayerId,
                moveTimeout: moveTimeout
            });
        } catch (e) {

        }
    }



    async function abortGame() {
        try {
            await api.post("/board/abortgame");
        } catch (e) {

        }
    }

    const [theme, setTheme] = useState("light");

    useEffect(() => {
        document.body.classList.remove("theme-dark", "theme-old");

        if (theme === "dark") document.body.classList.add("theme-dark");
        if (theme === "old") document.body.classList.add("theme-old");
    }, [theme]);


    async function makeMove(source, target) {
        await api.post("/board/makemove", { source: source, target: target });
    }

    function claculateNewBoardMoveIndex(maxIndex) {
        let baseBoard = [
            [2, 2, 2, 2, 2, 2, 2, 2],
            [2, 2, 2, 2, 2, 2, 2, 2],
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0],
            [1, 1, 1, 1, 1, 1, 1, 1],
            [1, 1, 1, 1, 1, 1, 1, 1],
        ];
        playedMove.slice(0, maxIndex).forEach((move, index) => {
            baseBoard[move.source.x][move.source.y] = 0
            baseBoard[move.target.x][move.target.y] = index % 2 === 0 ? 1 : 2;

        });

        return baseBoard;
    }

    const goToReplayIndex = (index) => {
        const clampedIndex = Math.max(0, Math.min(index, playedMove.length));

        const board = claculateNewBoardMoveIndex(clampedIndex);
        setBoard(board);

        setIsReplayMode(clampedIndex !== playedMove.length);
        setcurrentReplayIndex(clampedIndex);
        setHighlightedSquare(new Set());
    };

    function onClickPiece(coordinates) {
        if (isReplayMode) return;
        const squaresToHighlight = legalMoves.reduce((squares, current) => {
            if (current.source.x === coordinates.x && current.source.y === coordinates.y) {
                squares = squares.add(Map({ x: current.target.x, y: current.target.y }));
            }
            return squares;
        }, new Set().add(Map({ x: coordinates.x, y: coordinates.y })));

        setHighlightedSquare(squaresToHighlight);
    }

    async function onClickSquare(coordinates) {
        if (isReplayMode) return;
        const source = highlightedSquare.first();
        const target = Map({ x: coordinates.x, y: coordinates.y });

        if (typeof source !== "undefined" && !source.equals(target)) {

            if (highlightedSquare.has(target)) {
                await makeMove({ x: source.get("x"), y: source.get("y") }, coordinates);
            }

            setHighlightedSquare(new Set());
        }
    }

    function reversePlayers() {
        setSelectedWhitePlayerId(selectedBlackPlayerId);
        setSelectedBlackPlayerId(selectedWhitePlayerId);
    };

    return (
        <div className="layout">
            <div className="game-container">
                <div className="header">
                    <div>
                        <select className="selectLobby" value={theme} onChange={(e) => setTheme(e.target.value)}>
                            <option value="light">Light</option>
                            <option value="dark">Dark</option>
                            <option value="old">Old-fashioned</option>
                        </select>
                    </div>
                    <div>
                        <button className="button-custom-primary" onClick={null} title="Menu">Menu</button>
                        <select defaultValue={"Lobby 1"} className="selectLobby">
                            <option value="Lobby 1">Lobby 1</option>
                        </select>
                    </div>
                </div>
                <div className="main-content">
                    <aside className="sidebar">
                        <h3>Historique des coups</h3>
                        <div className="moves">
                            <History
                                currentIndex={currentReplayIndex}
                                playedMove={playedMove}
                                onClickHistoryMove={goToReplayIndex}
                            />
                        </div>
                        <div className="history-navigation">
                            <button className="button-custom-secondary" onClick={() => goToReplayIndex(currentReplayIndex - 1)} title="Voir le coup précedent"><i class="bi bi-arrow-left" ></i></button>
                            <button className="button-custom-secondary" onClick={() => goToReplayIndex(playedMove.length)} title="Revenir au coup courant"><i class="bi bi-arrow-down"></i></button>
                            <button className="button-custom-secondary" onClick={() => goToReplayIndex(currentReplayIndex + 1)} title="Voir le prochain coup"><i class="bi bi-arrow-right"></i></button>
                        </div>
                    </aside>

                    <div className="board-wrapper">
                        <Board
                            board={board}
                            legalMoves={isReplayMode ? [] : legalMoves}
                            highlightedSquare={highlightedSquare}
                            onClickPiece={onClickPiece}
                            onClickSquare={onClickSquare}
                        />
                    </div>
                </div>

                <div className="controls">
                    <div className="controls-row">
                        <label>
                            Joueur Blanc
                            <select value={selectedWhitePlayerId} onChange={e => setSelectedWhitePlayerId(e.target.value)}>
                                {readyPlayerIds.map(p => (
                                    <option key={p} value={p}>{p}</option>
                                ))}
                            </select>
                        </label>
                        <button className="button-custom-secondary" onClick={reversePlayers} title="Échanger les joueurs"><i className="bi bi-arrow-left-right"></i></button>
                        <label>
                            Joueur Noir
                            <select value={selectedBlackPlayerId} onChange={e => setSelectedBlackPlayerId(e.target.value)}>
                                {readyPlayerIds.map(p => (
                                    <option key={p} value={p}>{p}</option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Temps limite (ms)
                            <input
                                className="txtBox-custom-primary"
                                type="number"
                                value={moveTimeout}
                                onChange={e => setMoveTimeout(e.target.value)}
                            />
                        </label>
                    </div>

                    <div className="controls-row">
                        <div>
                            <button className="button-custom-primary" onClick={startGame}>Démarrer</button>
                            <button className="button-custom-secondary" style={{ marginLeft: "0.5rem" }} onClick={abortGame}>
                                Annuler
                            </button>
                        </div>
                        <label>
                            Meilleur de:
                            <select>
                            </select>
                        </label>
                        <p className="status-text">{gameStatus.description}</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Breakthrough;