package net.verdagon.vale.templar

import net.verdagon.vale._
import net.verdagon.vale.astronomer.ITemplexA
import net.verdagon.vale.templar.citizen.{ImplTemplar, StructTemplar}
import net.verdagon.vale.templar.env.{IEnvironment, IEnvironmentBox}
import net.verdagon.vale.templar.templata._
import net.verdagon.vale.templar.types._

import scala.collection.immutable.List
//import net.verdagon.vale.carpenter.CovarianceCarpenter
import net.verdagon.vale.scout.{IEnvironment => _, FunctionEnvironment => _, Environment => _, _}

object TypeTemplar {
  def convertExprs(
      env: IEnvironment,
      temputs: TemputsBox,
      sourceExprs: List[ReferenceExpression2],
      targetPointerTypes: List[Coord]):
  (List[ReferenceExpression2]) = {
    if (sourceExprs.size != targetPointerTypes.size) {
      vfail("num exprs mismatch, source:\n" + sourceExprs + "\ntarget:\n" + targetPointerTypes)
    }
    (sourceExprs zip targetPointerTypes).foldLeft((List[ReferenceExpression2]()))({
      case ((previousRefExprs), (sourceExpr, targetPointerType)) => {
        val refExpr =
          convert(env, temputs, sourceExpr, targetPointerType)
        (previousRefExprs :+ refExpr)
      }
    })
  }

  def convert(
      env: IEnvironment,
      temputs: TemputsBox,
      sourceExpr: ReferenceExpression2,
      targetPointerType: Coord):
  (ReferenceExpression2) = {
    val sourcePointerType = sourceExpr.resultRegister.reference

    val Coord(targetOwnership, targetType) = targetPointerType;
    val Coord(sourceOwnership, sourceType) = sourcePointerType;

    if (sourceExpr.resultRegister.reference == targetPointerType) {
      return sourceExpr
    }

    if (sourceExpr.resultRegister.reference.referend == Never2()) {
      return sourceExpr
    }

    vcurious(targetPointerType.referend != Never2())

    // We make the hammer aware of nevers.
//    if (sourceType == Never2()) {
//      return (TemplarReinterpret2(sourceExpr, targetPointerType))
//    }

    val sourceExprDecayedOwnershipped =
      (sourceOwnership, targetOwnership) match {
        case (Own, Own) => sourceExpr
        case (Borrow, Own) => {
          vfail("Supplied a borrow but target wants to own the argument")
        }
        case (Own, Borrow) => {
          vfail("Supplied an owning but target wants to only borrow")
        }
        case (Borrow, Borrow) => sourceExpr
        case (Share, Share) => sourceExpr
        case (Own, Share) => {
          vfail(); // curious
        }
        case (Borrow, Share) => {
          vfail(); // curious
        }
      }

    val sourceExprDecayedOwnershippedConverted =
      if (sourceType == targetType) {
        (sourceExprDecayedOwnershipped)
      } else {
        (sourceType, targetType) match {
          case (s @ StructRef2(_), i : InterfaceRef2) => {
            StructTemplar.convert(env.globalEnv, temputs, sourceExprDecayedOwnershipped, s, i)
          }
          case _ => vfail()
        }
      };

    (sourceExprDecayedOwnershippedConverted)
  }
}