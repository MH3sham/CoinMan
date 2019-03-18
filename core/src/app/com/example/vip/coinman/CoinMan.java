package app.com.example.vip.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState, pause;
	float gravity = 0.2f;
	float velocity = 0;
	int manX = 0;
	int manY = 0;
	Rectangle manRectangle;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount = 0;
	Random random;
	int coinWidth , coinHight;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount = 0;
	int bombWidth , bombHight;

	int score = 0;
	BitmapFont scoreFont, tabtoStartFont ,gameoverFont;
	int gameState = 1;

	Texture dizzy;
	public static AssetManager assetManager; //audio
	Sound coinSound;
	Music bombSound, backgroundMusic;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		dizzy = new Texture("dizzy-1.png");

		manRectangle = new Rectangle();

		manX = Gdx.graphics.getWidth()/2;
		manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		coinWidth = Gdx.graphics.getWidth()/8;
        coinHight = Gdx.graphics.getWidth()/8;

		bomb = new Texture("bomb.png");
		bombWidth = Gdx.graphics.getWidth()/8;
		bombHight = Gdx.graphics.getWidth()/8;

        random = new Random();

        //score scoreFont
        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(3);

		//GameOver Font
		gameoverFont = new BitmapFont();
		gameoverFont.setColor(Color.RED);
		gameoverFont.getData().setScale(4);

		//audio
		assetManager = new AssetManager();
		assetManager.load("coin.wav", Sound.class);
		assetManager.load("bomb.wav", Music.class);
		assetManager.load("background.mp3" , Music.class);
		assetManager.finishLoading();

    }

	public void makeCoin(){
		float hight = random.nextFloat() * Gdx.graphics.getHeight();  //0.2 x 5  >> 1
		coinYs.add((int) hight);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float hight = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) hight);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () { //loop method keeps running
		batch.begin();
		//draw back ground with the hight and width of screen size starting at pos 0 , 0.
		batch.draw(background,0,0, Gdx.graphics.getWidth() , Gdx.graphics.getHeight());

		if (gameState == 0)
		{
			//Game Is LIVE
            backgroundMusic = assetManager.get("background.mp3", Music.class); //play background sound
            backgroundMusic.setLooping(true);
            backgroundMusic.play();

			//BOMB
			if (bombCount < 150){
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb(); //adds xPos and Ypos to arrays.
			}
			bombRectangle.clear();
			//we dont know how many coins added to the array so we gonna loop throw it and draw a coin at every pos saved.
			for (int i = 0 ; i<bombXs.size() ; i++){
				batch.draw(bomb , bombXs.get(i), bombYs.get(i) ,bombWidth ,bombHight);
				bombXs.set(i , bombXs.get(i) - 8); //after drawing at x pos go and - it from 8 so that will make it if it's 10 >> 2 so coin will move from 10 xPos to 2 xPos and will appear to be moving as render method keeps looping.
				bombRectangle.add(new Rectangle(bombXs.get(i) , bombYs.get(i), bombWidth , bombHight));

			}


			//COIN
			if (coinCount < 100){
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin(); //adds xPos and Ypos to arrays.
			}

			coinRectangle.clear();
			//we dont know how many coins added to the array so we gonna loop throw it and draw a coin at every pos saved.
			for (int i = 0 ; i<coinXs.size() ; i++){
				batch.draw(coin , coinXs.get(i), coinYs.get(i) ,coinWidth ,coinHight);
				coinXs.set(i , coinXs.get(i) - 4);//after drawing at x pos go and - it from 4 so that will make it if it's 10 >> 6 so coin will move from 10 xPos to 6 xPos
				coinRectangle.add(new Rectangle(coinXs.get(i) , coinYs.get(i), coinWidth , coinHight));
			}

			//JUMB ON TOUCH
			if (Gdx.input.justTouched()){
				velocity = -10; //jumb when touch
			}
			if (pause < 8){ //works as a timer so man dont change state fast
				pause++;
			}else {
				pause=0;
				if (manState < 3){
					manState++;
				}else {
					manState =0;
				}
			}

			//GRAVITY
			velocity = velocity + gravity;  // zwd l sor3a b mgdar l gazbya (ynzel bsor3a 0.2 > 0.4 > 0.8 ....)
			manY = (int) (manY - velocity); // etra7 b2a l sor3a de mn l Y as he 'll start at the top on Y .

			if (manY <= 0){ //max fall is at y=0 so he will fall at the end of screen
				manY = 0;
			}else if (manY >= Gdx.graphics.getHeight() + 300 - man[manState].getHeight()){
				manY = Gdx.graphics.getHeight() + 300 - man[manState].getHeight();
			}
		}
		else if (gameState == 1)
		{
			//Waiting To Start
			if (Gdx.input.justTouched()){
				gameState = 0;
			}
		}
		else if (gameState == 2)
		{
			//Game Over
			if (Gdx.input.justTouched()){
				gameState = 0;
				manX = Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount = 0;
				bombSound.stop();
			}
		}

		if (gameState == 2){
			//collision bomb
			//draw man dizzy
			batch.draw(dizzy, manX , manY, Gdx.graphics.getWidth()/5 , Gdx.graphics.getHeight()/6);
			gameoverFont.draw(batch , "Game Over!" ,Gdx.graphics.getWidth()/2 - 170 , Gdx.graphics.getHeight()/2 + 100);
			scoreFont.draw(batch , "Your Score: " + String.valueOf(score) ,Gdx.graphics.getWidth()/2-150 , Gdx.graphics.getHeight()/2 - 30);
			backgroundMusic.stop();
			bombSound = assetManager.get("bomb.wav", Music.class); //play game over sound
			bombSound.play();

		}else {
			//draw man
			batch.draw(man[manState], manX , manY, Gdx.graphics.getWidth()/5 , Gdx.graphics.getHeight()/6);
			scoreFont.draw(batch , "Score: " + String.valueOf(score) ,Gdx.graphics.getWidth()-200 , Gdx.graphics.getHeight()-20);
		}

		manRectangle= new Rectangle(manX , manY, Gdx.graphics.getWidth()/5 , Gdx.graphics.getHeight()/6);

		//checks if Rectangle around man crashes Rectangle around Coins
		for (int i = 0 ; i < coinRectangle.size() ; i++){
			if (Intersector.overlaps(manRectangle , coinRectangle.get(i))){
				Gdx.app.log("Coin!", "Collision");
				score++;
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				coinSound = assetManager.get("coin.wav", Sound.class); //play coin sound
				coinSound.play();
				break;
			}
		}

		//checks if Rectangle around man crashes Rectangle around Bombs
		for (int i = 0 ; i < bombRectangle.size() ; i++){
			if (Intersector.overlaps(manRectangle , bombRectangle.get(i))){
				Gdx.app.log("Bomb!", "Collision");
				gameState = 2;

			}
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
