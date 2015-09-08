// Generated from /home/cristian/workspace/mc1/src/main/java/org/cifasis/mc1/Poet.g4 by ANTLR 4.5.1
package org.cifasis.mc1;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PoetParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PoetVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PoetParser#events}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvents(PoetParser.EventsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent(PoetParser.EventContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#eventList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventList(PoetParser.EventListContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#pred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPred(PoetParser.PredContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#succ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSucc(PoetParser.SuccContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#icnf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIcnf(PoetParser.IcnfContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#disa}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisa(PoetParser.DisaContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#alte}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlte(PoetParser.AlteContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#evtr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvtr(PoetParser.EvtrContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#proc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProc(PoetParser.ProcContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#tr_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTr_id(PoetParser.Tr_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#acts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActs(PoetParser.ActsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PoetParser#actName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActName(PoetParser.ActNameContext ctx);
}