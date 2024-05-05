package com.broll.gainea;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Action_Shop;
import com.broll.gainea.net.NT_AddBot;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Intention;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Roll;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_Event;
import com.broll.gainea.net.NT_Event_BoardEffect;
import com.broll.gainea.net.NT_Event_BoughtMerc;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Event_FocusLocation;
import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.net.NT_Event_FocusObjects;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedCard;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal;
import com.broll.gainea.net.NT_Event_PlacedObject;
import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.net.NT_Event_ReceivedCard;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedPoints;
import com.broll.gainea.net.NT_Event_ReceivedStars;
import com.broll.gainea.net.NT_Event_RemoveCard;
import com.broll.gainea.net.NT_Event_RemoveGoal;
import com.broll.gainea.net.NT_Event_RemoveObjects;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.net.NT_Event_UpdateObjects;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_GameStatistic;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_GoalProgression;
import com.broll.gainea.net.NT_Lib;
import com.broll.gainea.net.NT_Lib_Card;
import com.broll.gainea.net.NT_Lib_Fraction;
import com.broll.gainea.net.NT_Lib_Goal;
import com.broll.gainea.net.NT_Lib_Monster;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerChangeFraction;
import com.broll.gainea.net.NT_PlayerReady;
import com.broll.gainea.net.NT_PlayerSettings;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_PlayerTurnStart;
import com.broll.gainea.net.NT_PlayerWait;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_ReconnectGame;
import com.broll.gainea.net.NT_RoundStatistic;
import com.broll.gainea.net.NT_Shop;
import com.broll.gainea.net.NT_ShopItem;
import com.broll.gainea.net.NT_ShopOther;
import com.broll.gainea.net.NT_ShopUnit;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.net.NT_Surrender;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.net.NT_UpdateLobbySettings;
import com.broll.gainea.net.NT_Vote;
import com.broll.gainea.net.NT_VotePending;
import com.broll.gainea.net.NT_Vote_Base;
import com.broll.gainea.net.NT_Vote_KickPlayer;
import com.broll.gainea.net.NT_Vote_SkipTurn;
import com.broll.networklib.NetworkRegister;
import com.google.common.collect.Lists;

import java.util.List;

public class NetworkSetup {


    public static void registerNetwork(NetworkRegister register) {
        List<Class> classes = Lists.newArrayList(
                NT_Action.class, NT_Action_Attack.class, NT_Action_Card.class, NT_Action_Shop.class,
                NT_Action_Move.class, NT_Action_PlaceUnit.class, NT_Action_SelectChoice.class,
                NT_AddBot.class, NT_Battle_Roll.class, NT_Battle_Intention.class, NT_Battle_Reaction.class,
                NT_Battle_Damage.class, NT_Surrender.class, NT_Shop.class,
                NT_Battle_Start.class, NT_Battle_Update.class, NT_BoardEffect.class,
                NT_BoardObject.class, NT_BoardUpdate.class, NT_Card.class,
                NT_EndTurn.class, NT_Event.class, NT_Event_BoardEffect.class,
                NT_Event_FinishedGoal.class, NT_Event_FocusLocation.class, NT_Event_FocusObject.class,
                NT_Event_FocusObjects.class, NT_Event_MovedObject.class, NT_Event_OtherPlayerReceivedCard.class,
                NT_Event_OtherPlayerReceivedGoal.class, NT_Event_PlacedObject.class, NT_Event_PlayedCard.class,
                NT_Event_ReceivedCard.class, NT_Event_ReceivedGoal.class, NT_Event_ReceivedGoal.class,
                NT_Event_ReceivedPoints.class, NT_Event_ReceivedStars.class, NT_Event_RemoveCard.class,
                NT_Event_RemoveGoal.class, NT_Event_RemoveObjects.class, NT_Event_TextInfo.class, NT_Event_UpdateObjects.class,
                NT_GameOver.class, NT_GameStatistic.class, NT_Goal.class, NT_GoalProgression.class, NT_Event_BoughtMerc.class,
                NT_LoadedGame.class, NT_LobbySettings.class, NT_Monster.class,
                NT_Player.class, NT_PlayerAction.class, NT_PlayerChangeFraction.class,
                NT_PlayerReady.class, NT_PlayerSettings.class, NT_PlayerTurnActions.class,
                NT_PlayerTurnStart.class, NT_PlayerWait.class, NT_Reaction.class,
                NT_ReconnectGame.class, NT_RoundStatistic.class, NT_StartGame.class,
                NT_Unit.class, NT_UpdateLobbySettings.class, NT_Battle_Roll[].class, NT_Battle_Damage[].class,
                NT_Action[].class, NT_BoardObject[].class, NT_Unit[].class, NT_Player[].class,
                NT_Goal[].class, NT_Card[].class, Integer[].class, int[].class, short[].class, byte[].class,
                String[].class, Object[].class, NT_RoundStatistic[].class, NT_BoardEffect[].class,
                NT_Vote.class, NT_Vote_KickPlayer.class, NT_Vote_SkipTurn.class,
                NT_Vote_Base.class, NT_VotePending.class,
                NT_ShopItem.class, NT_ShopItem[].class, NT_ShopOther.class, NT_ShopUnit.class,
                NT_Lib.class, NT_Lib_Fraction[].class, NT_Lib_Fraction.class, NT_Lib_Card[].class, NT_Lib_Card.class,
                NT_Lib_Monster[].class, NT_Lib_Monster.class, NT_Lib_Goal[].class, NT_Lib_Goal.class
        );
        classes.forEach(register::registerNetworkType);
    }
}
