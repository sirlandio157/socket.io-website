<<<<<<< SEARCH
    <title>Jogo de Plataforma</title>
=======
    <title>Jogo da Velha Online</title>
>>>>>>> REPLACE
<<<<<<< SEARCH
        #game-container {
            position: relative;
            width: 800px;
            height: 400px;
            margin: 0 auto;
            background-color: #222;
            overflow: hidden;
            border: 4px solid #333;
            border-radius: 8px;
            box-shadow: 0 0 20px rgba(0,0,0,0.5);
        }
=======
        #game-container {
            position: relative;
            width: 300px;
            height: 300px;
            margin: 50px auto;
            background-color: #f0f0f0;
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            grid-template-rows: repeat(3, 1fr);
            gap: 5px;
            padding: 10px;
            border-radius: 8px;
            box-shadow: 0 0 20px rgba(0,0,0,0.2);
        }
>>>>>>> REPLACE
<<<<<<< SEARCH
            <h1>Jogo de Plataforma</h1>
            <p>Colete moedas e pule os obstáculos!</p>
            <p>Use SPACE para pular e setas para mover</p>
            <button id="start-button">Começar</button>
=======
            <h1>Jogo da Velha Online</h1>
            <p>Conecte-se e jogue com um amigo!</p>
            <input type="text" id="room-id" placeholder="ID da Sala">
            <button id="create-button">Criar Sala</button>
            <button id="join-button">Entrar na Sala</button>
            <div id="status-message"></div>
>>>>>>> REPLACE

<<<<<<< SEARCH
        <div id="player"></div>
        <div id="score">Pontos: 0</div>
=======
        <div id="score"></div>
        <div class="cell"></div>
        <div class="cell"></div>
        <div class="cell"></div>
        <div class="cell"></div>
        <div class="cell"></div>
        <div class="cell"></div>
        <div class="cell"></div>
        <div class="cell"></div>
>>>>>>> REPLACE
<<<<<<< SEARCH
        .obstacle {
            position: absolute;
            background-color: #795548;
            border-radius: 5px;
        }
=======
        .cell {
            background-color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 48px;
            cursor: pointer;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        
        .cell:hover {
            background-color: #e0e0e0;
        }
        
        .cell.x {
            color: #FF5252;
        }
        
        .cell.o {
            color: #2196F3;
        }
>>>>>>> REPLACE
