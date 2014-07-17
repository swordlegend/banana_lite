
#include "Game_Config.h"

package PACKAGE_NAME;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.lcdui.game.Sprite;
import java.util.Vector;
import java.util.Random;

class EnemiesManager
{
	public static final int TYPE_GOBLIN = 0;
	public static final int TYPE_TROLL = 1;
	public static final int TYPE_THROWER = 2;
	public static final int TYPE_SPIDER = 3;
	public static final int TYPE_RAVEN = 4;
	public static final int TYPE_SHAMAN = 5;
	public static final int TYPE_OGRE = 6;
	public static final int TYPE_NUM = 7;
	
	private static final int MAX_ENEMIES = 10;
	
	public static int WALL_MIN_X = -160;
	public static int WALL_MAX_X = 160;	
	
	public static EnemiesManager Instance = null;
	
	public EnemiesManager()
	{
		Instance = this;
		
		random = new Random();
		
		listEnemies = new Enemy[MAX_ENEMIES];
		listEnemiesToPaintBehindWall = new Enemy[MAX_ENEMIES];
		listEnemiesToPaintBeforeWall = new Enemy[MAX_ENEMIES];
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			listEnemies[i] = new Enemy();
		}
	}
	
	public void loadAssets()
	{
		if( spritesPool == null )
		{
			spritesPool = new ASprites[TYPE_NUM];
			animFramesCount = new byte[TYPE_NUM][];			
			loadSprite( TYPE_GOBLIN, "/goblin.png", "/goblin.dat" );
			
			EnemyPropertiesPool = new EnemyProperties[TYPE_NUM];
			EnemyPropertiesPool[TYPE_GOBLIN] = new EnemyProperties(32);
		}
	}
	
	private void loadSprite(int type, String imageFileName, String dataFileName)
	{
		spritesPool[type] = new ASprites();
		spritesPool[type].loadSprite( imageFileName, dataFileName );
		
		final int count = spritesPool[type].animsCount;
		animFramesCount[type] = new byte[count];
		for( int i=0; i<count; ++i )
		{
			animFramesCount[type][i] = (byte)spritesPool[type].getAnimFrameCount(i);
		}
	}
	
	public void update()
	{
		int i;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( !listEnemies[i].isActive )
			{
				break;
			}
		}
		
		if( i < MAX_ENEMIES )
		{
			listEnemies[i].reset(TYPE_GOBLIN);			
			listEnemies[i].x = ((random.nextInt() & 127) < 64 ? WALL_MIN_X : WALL_MAX_X) << 4;			
			listEnemies[i].y = random.nextInt() & 255;
			listEnemies[i].flip = listEnemies[i].x < 0 ? Sprite.TRANS_NONE : Sprite.TRANS_MIRROR;
		}
		
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive )
			{
				listEnemies[i].update();
			}
			
			listEnemiesToPaintBehindWall[i] = null;
			listEnemiesToPaintBeforeWall[i] = null;
		}
		
		// Sort behind wall enemies
		int count = 0;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive && listEnemies[i].isBehindWall )
			{
				listEnemiesToPaintBehindWall[count] = listEnemies[i];
				++count;
			}
		}
		sortEnemiesByY(listEnemiesToPaintBehindWall, count);
		
		count = 0;
		for( i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive && !listEnemies[i].isBehindWall )
			{
				listEnemiesToPaintBeforeWall[count] = listEnemies[i];
				++count;
			}
		}
		sortEnemiesByY(listEnemiesToPaintBeforeWall, count);
	}	
	
	private void sortEnemiesByY(Enemy[] list, int count)
	{
		for( int i=0; i<count; ++i )
		{
			for( int j=i+1; j<count; ++j )
			{
				if( list[j].y > list[i].y )
				{
					Enemy temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
	}
	
	public void paintBehindWall(Graphics g)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemiesToPaintBehindWall[i];
			if( enemy != null )
			{
				enemy.paint(g);
			}
			else
			{
				break;
			}
		}
	}
	
	public void paintBeforeWall(Graphics g)
	{
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			Enemy enemy = listEnemiesToPaintBeforeWall[i];
			if( enemy != null )
			{
				enemy.paint(g);
			}
			else
			{
				break;
			}
		}
	}
	
	public int serialize(byte[] data, int offset)
	{
		int count = 0;
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive )
			{
				++count;
			}
		}
		
		data[offset+0] = (byte)count;
		++offset;
		
		for( int i=0; i<MAX_ENEMIES; ++i )
		{
			if( listEnemies[i].isActive )
			{
				offset = listEnemies[i].serialize(data, offset);
			}
		}
	
		return offset;
	}
	
	public int deserialize(byte[] data, int offset)
	{	
		if( data != null )
		{
			for( int i=0; i<MAX_ENEMIES; ++i )
			{
				listEnemies[i].isActive = false;
			}
			
			int count = data[offset+0];
			++offset;
			
			for( int i=0; i<count; ++i )
			{
				offset = listEnemies[i].deserialize(data, offset);
			}
		
			return offset;
		}
		
		return 0;
	}
	
	public ASprites[] spritesPool;
	public byte[][] animFramesCount;
	
	private Enemy[] listEnemies;
	private Enemy[] listEnemiesToPaintBehindWall;
	private Enemy[] listEnemiesToPaintBeforeWall;
	
	public EnemyProperties[] EnemyPropertiesPool;
	private Random random;
}