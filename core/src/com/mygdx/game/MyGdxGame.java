package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.xml.xpath.XPath;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	//Atributos de ação
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Music musicafundodia;

	//Atributos de configuração
	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo=0;  // 0->Não inicado | 1->Iniciado  | 2->Fim de jogo
	private int pontuacao=0;

	private Circle passaroCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;

	private Random numeroRandomico;

	private float variacao = 0;
	private float velocidadeQueda=0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto=false;

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(3);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		musicafundodia = Gdx.audio.newMusic(Gdx.files.internal("musica_dia.mp3"));
		musicafundodia.setLooping(estadoJogo == 1);


		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");

		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;

		posicaoInicialVertical = alturaDispositivo/2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 300;

	}

	@Override
	public void render () {
		camera.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;

		//Alteração Variação
		if(variacao > 3) variacao = 0;

		if(estadoJogo == 0){
			if(Gdx.input.justTouched()){
				estadoJogo = 1;
			}
		}else {
			velocidadeQueda++;
			if(posicaoInicialVertical > 0 || velocidadeQueda <0){
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
			}

			if(estadoJogo == 1){

				musicafundodia.play();
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				if(Gdx.input.justTouched()){
					velocidadeQueda = -15;
				}

				if(posicaoMovimentoCanoHorizontal <- canoTopo.getWidth()){
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400)-200;
					marcouPonto = false;
				}

				if(posicaoMovimentoCanoHorizontal < 120){
					if(!marcouPonto){
						pontuacao++;
						marcouPonto=true;
					}
				}

			}else{
				if(Gdx.input.justTouched()){
					fundo = new Texture("fundo.png");
					estadoJogo=0;
					velocidadeQueda=0;
					pontuacao=0;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					posicaoInicialVertical = alturaDispositivo/2;
				}
			}


		}

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
			batch.draw(fundo, 0,0, larguraDispositivo, alturaDispositivo);
			batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica);
			batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 -canoBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomica);
			batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
			fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo-50);

			if(pontuacao == 5){
				fundo = new Texture("fundo_noite.png");
				// COLOCAR NOVA MUSICA AQUI

				musicafundodia.setLooping(false);
				Music musicafundonoite = Gdx.audio.newMusic(Gdx.files.internal("musica_noite.mp3"));
				musicafundonoite.setLooping(true);
				musicafundonoite.play();
			}

			if(estadoJogo == 2){
				mensagem.draw(batch, "Clique para reiniciar!", larguraDispositivo/2-230, alturaDispositivo/2 - gameOver.getHeight());
				batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
			}
		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
		retanguloCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBaixo.getHeight()-espacoEntreCanos/2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2+alturaEntreCanosRandomica,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo) ||
		   posicaoInicialVertical <=0 || posicaoInicialVertical >= alturaDispositivo ){
			estadoJogo = 2;
		}

	}

	@Override
	public void resize(int width, int height){
		viewport.update(width,height);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		passaros[0].dispose();
		passaros[1].dispose();
		passaros[2].dispose();
		fundo.dispose();
		canoBaixo.dispose();
		canoTopo.dispose();
		gameOver.dispose();
	}
}

