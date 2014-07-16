
private static final int MAIN_MENU_BASE = 0;
private static final int MAIN_MENU_LEADERBOARD = 0;
private static final int MAIN_MENU_HELP = 0;

private Image imgTitle;
private GameMenu menuMain;

private int currentMainMenu;

private void initMainMenu()
{
	currentMainMenu = MAIN_MENU_BASE;
	imgTitle = Util.loadImage( "/title.png" );	
	
	if( imgWall == null )
	{
		imgWall = Util.loadImage( "/wall.png" );	
		wallHeight = imgWall.getHeight() - 16;
	}

	menuMain = new GameMenu( 3, GameMenu.kBarTypeShort );
	menuMain.setItem( 0, STR_NEWGAME );
	menuMain.setItem( 1, STR_SOUND_ON );
	menuMain.setItem( 2, STR_HELP );
	menuMain.soundItem = 1;
	
	menuMain.init();
	
	setLeftSoftkey(kSoftkeyYes);
	setRightSoftkey(kSoftkeyQuit);
}

private void destroyMainMenu()
{
	menuMain = null;	
	imgTitle = null;
}

private void updateMainMenu()
{
	if( updateMenu( menuMain ) )
	{	
		switch( menuMain.select )
		{
		case 0:
			changeState( k_State_InGame );		
		break;
		case 1:
			optionSound = optionSound == 0 ? 1 : 0;
		break;
		case 2:
		break;
		}
		
		playSfx(SFX_MENU_HIT);
	}
	
	if( isRightSK )
	{
		quitGame();
	}
}

private void paintMainMenu()
{
	currentGraphics.setColor( 0x70c7ed );
	currentGraphics.fillRect( 0, 0, canvasWidth, canvasHeight - wallHeight );

	currentGraphics.drawImage( imgWall, 
		( canvasWidth - imgWall.getWidth() ) >> 1, 
		( canvasHeight - imgWall.getHeight() ), 0 );
		
	menuMain.paint( canvasWidth >> 1, canvasHeight / 3 );
	
	currentGraphics.drawImage( imgTitle, 0, 0, 0 );	
	
	int y = canvasHeight - (font.fontHeight << 1) - (font.fontHeight >> 1);
	font.drawString( STR_HIGHSCORE, canvasWidth >> 1, y, AFont.kAlignCenter );	
	font.drawNumber( highScore, canvasWidth >> 1, y + font.fontHeight, AFont.kAlignCenter );	
}
