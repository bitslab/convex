package convex.core.lang;

import static convex.test.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import convex.core.Constants;
import convex.core.ErrorCodes;
import convex.core.data.BlobMaps;
import convex.core.data.Symbol;
import convex.core.data.Syntax;

/**
 * Tests for execution context mechanics and internals
 */
public class ContextTest {

	private final Context<?> CTX = TestState.INITIAL_CONTEXT.fork();

	@Test
	public void testDefine() {
		Symbol sym = Symbol.create("the-test-symbol");

		final Context<?> c2 = CTX.fork().define(sym, Syntax.create("buffy"));
		assertEquals("buffy", c2.lookup(sym).getResult());

		assertUndeclaredError(c2.lookup(Symbol.create("some-bad-symbol")));
	}
	
	@Test
	public void testUndefine() {
		Symbol sym = Symbol.create("the-test-symbol");

		final Context<?> c2 = CTX.fork().define(sym, Syntax.create("vampire"));
		assertEquals("vampire", c2.lookup(sym).getResult());

		final Context<?> c3 = c2.undefine(sym);
		assertUndeclaredError(c3.lookup(sym));
		
		final Context<?> c4 = c3.undefine(sym);
		assertSame(c3,c4);
	}
	
	@Test
	public void testExceptionalState() {
		Context<?> ctx=CTX.fork();
		
		assertFalse(ctx.isExceptional());
		assertTrue(ctx.withError(ErrorCodes.ASSERT).isExceptional());
		assertTrue(ctx.withError(ErrorCodes.ASSERT,"Assert Failed").isExceptional());
		
		assertThrows(IllegalArgumentException.class,()->ctx.withError(null));
		
		assertThrows(Error.class,()->ctx.withError(ErrorCodes.ASSERT).getResult());
	}

	@Test
	public void testJuice() {
		Context<?> c=CTX.fork();
		assertTrue(c.checkJuice(1000));
		
		// get a juice error if too much juice consumed
		assertJuiceError(c.consumeJuice(c.getJuice() + 1));
		
		// no error if all juice is consumed
		c=CTX.fork();
		assertFalse(c.consumeJuice(c.getJuice()).isExceptional());
	}

	@Test
	public void testSpecial() {
		Context<?> ctx=CTX.fork();
		assertEquals(TestState.HERO, ctx.computeSpecial(Symbols.STAR_ADDRESS).getResult());
		assertEquals(TestState.HERO, ctx.computeSpecial(Symbols.STAR_ORIGIN).getResult());
		assertNull(ctx.computeSpecial(Symbols.STAR_CALLER).getResult());
		
		assertNull(ctx.computeSpecial(Symbols.STAR_RESULT).getResult());
		assertCVMEquals(ctx.getJuice(), ctx.computeSpecial(Symbols.STAR_JUICE).getResult());
		assertCVMEquals(0L,ctx.computeSpecial(Symbols.STAR_DEPTH).getResult());
		assertCVMEquals(ctx.getBalance(TestState.HERO),ctx.computeSpecial(Symbols.STAR_BALANCE).getResult());
		assertCVMEquals(0L,ctx.computeSpecial(Symbols.STAR_OFFER).getResult());
		
		assertCVMEquals(0L,ctx.computeSpecial(Symbols.STAR_SEQUENCE).getResult());

		assertCVMEquals(Constants.INITIAL_TIMESTAMP,ctx.computeSpecial(Symbols.STAR_TIMESTAMP).getResult());
		
		assertSame(ctx.getState(), ctx.computeSpecial(Symbols.STAR_STATE).getResult());
		assertSame(BlobMaps.empty(),ctx.computeSpecial(Symbols.STAR_HOLDINGS).getResult());
		
		assertUndeclaredError(ctx.eval(Symbol.create("*bad-special-symbol*")));
		assertNull(ctx.computeSpecial(Symbol.create("count")));
	}

	@Test
	public void testEdn() {
		Context<?> ctx=CTX.fork();
		String s = ctx.ednString();
		assertNotNull(s);
	}

	@Test
	public void testReturn() {
		Context<?> ctx=CTX.fork();
		ctx = ctx.withResult(Long.valueOf(100));
		assertEquals(ctx.getDepth(), ctx.getDepth());
	}

}
