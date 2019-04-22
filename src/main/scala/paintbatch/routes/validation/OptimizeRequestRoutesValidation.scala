package com.paintbatch.routes

import com.paintbatch.models.{OptimizeRequest, ReadableError, Demand}

object OptimizeRequestRoutesValidation {
  private val MAX_COLORS = sys.env.getOrElse("MAX_COLORS", "2000").toInt
  private val MAX_CUSTOMERS = sys.env.getOrElse("MAX_CUSTOMERS", "2000").toInt
  private val MAX_CUSTOMER_CHOICES = sys.env.getOrElse("MAX_CUSTOMER_CHOICES", "3000").toInt

  def validate(optimize: OptimizeRequest): Option[ReadableError] = {
    optimize match {
      case OptimizeRequest(colors, _ , _) if colors < 1 || colors > MAX_COLORS => {
        val message = s"$colors colors are requested, you can only request colors from 1 to a max of $MAX_COLORS"
        Some(ReadableError(message))
      }
      case OptimizeRequest(_, customers , _) if customers > MAX_CUSTOMERS => {
        val message = s"$customers customers demands are to be met, you can only request customers from 1 to a max of $MAX_CUSTOMERS"
        Some(ReadableError(message))
      }
      case OptimizeRequest(colors, _ , demands) => {
        var customerChoices = 0

        demands.foldLeft(Option.empty[ReadableError])((brokenConstraint, demand) => {
          brokenConstraint match {
            case None => {
              customerChoices = customerChoices + demand.paints.length
              if (customerChoices > MAX_CUSTOMER_CHOICES) {
                val message = s"customer choices are too high, you can only request a max of $MAX_CUSTOMER_CHOICES customer choices"
                Some(ReadableError(message))
              }
    
              demand.paints.foldLeft(Option.empty[ReadableError])((brokenConstraint, paint) => {
                import com.paintbatch.models.Sheen
                import com.paintbatch.models.Sheen.UNDEFINED
                
                brokenConstraint match {
                  case None => {
                    if (paint.id < 1 || paint.id > colors) {
                      val message = s"color ${paint.id} which a customer picked is outside the colors requested"
                      Some(ReadableError(message))
                    }
                    else{
                      paint.sheen match {
                        case UNDEFINED(value) => {
                          val message = s"sheen ${value} which a customer picked is not matte or gloss"
                          Some(ReadableError(message))
                        }
                        case _ => None
                      }
                    }
                  }
                  case _ => brokenConstraint
                }
              }) 
            }
            case _ => brokenConstraint
          }
        })
      }
    }
  }
}