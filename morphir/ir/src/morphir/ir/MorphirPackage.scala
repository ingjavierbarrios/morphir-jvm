package morphir.ir

import io.estatico.newtype.macros.newtype
import morphir.ir.codec.packageCodecs
import morphir.ir.module.ModulePath
import morphir.ir.path.Path

object MorphirPackage {

  @newtype case class PackagePath(toPath: path.Path)
  object PackagePath {
    import upickle.default._

    implicit val readWriter: ReadWriter[PackagePath] = readwriter[Path].bimap(
      pkgPath => pkgPath.toPath,
      path => PackagePath(path)
    )
  }

  sealed trait PackageRef
  object PackageRef {
    case object ThisPackage                                        extends PackageRef
    final case class PackageDependency(toPackagePath: PackagePath) extends PackageRef
  }

  case class Pkg[+A](
    dependencies: Map[PackagePath, Specification[A]],
    modules: Map[ModulePath, AccessControlled[module.Definition[A]]]
  ) {
    def toPackageDefinition: PkgDef[A] = Definition[A](dependencies, modules)
  }

  object Pkg {}

  final case class Specification[+A](modules: Map[ModulePath, module.Specification[A]])
  object Specification extends packageCodecs.SpecificationCodec {

    def empty[A]: Specification[A] = Specification[A](Map.empty)
  }

  final case class PackageSpecEntry[+A](name: ModulePath, spec: module.Specification[A])

  final case class Definition[+A](
    dependencies: Map[PackagePath, Specification[A]],
    modules: Map[ModulePath, AccessControlled[module.Definition[A]]]
  )

  object Definition extends packageCodecs.DefinitionCodec {
    def empty[A]: Definition[A] = Definition(Map.empty, Map.empty)
  }

  @inline def emptySpecification[A]: Specification[A] = Specification.empty
  @inline def emptyDefinition[A]: Definition[A]       = Definition.empty

}
