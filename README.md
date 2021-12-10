# Cartographer
Semiautomatic PHP code generator.

Cartographer can read a specification file for a PHP system and automatically generate files, folders and code, as well as a default `composer.json` file. Folder and file layout follows the `PSR-4` convention.

### Features

* single specification file for the whole project
* simple, minimalistic syntax
* automatic `get` and `set` methods for attributes
* automatic `__construct` method with dependency injection
* supports visibility, `static` and `const` modifiers
* supports type annotations and namespaces
* supports `class`, `abstract class`, `interface` and `trait`, as well as `extends` and `implements` keywords
* automatic `composer.json` file generation with `PSR-4` autoloader
* automatic type overview chart in `.dot` format

## Requirements

* scala compiler
* SBT

## Execution

The following will read specification file in `./my-system.cg` and generate code under `./my-system` folder (`my-system` will be created automatically):

`sbt "run my-system.cg my-system"`

yielding the following directory layout:

```
+my-system
|  src
|  composer.json
|  type-chart.dot
```

where `src` will contain additional folders (one for each namespace) and file (one for each class, abstract class, interface or trait).

## Input Format

Specification file must be a production of the following context free grammar:

```
<S> ::= <Root> <NamespaceList>
    | base CamelIdentifier <NamespacePath> <Root> <NamespaceList>

<NamespacePath> ::=
    | / CamelIdentifier <NamespacePath>

Root ::=
    | <Class> <ClassList>

<NamespaceList> ::= 
    | namespace CamelIdentifier <NamespacePath> <ClassList> <NamespaceList>

<Class> ::= class    CamelIdentifier <MaybeExtend> <MaybeImplement> <FieldList>
    | abstract class CamelIdentifier <MaybeExtend> <MaybeImplement> <FieldList>
    | interface      CamelIdentifier <MaybeExtend> <MaybeImplement> <FieldList>
    | trait          CamelIdentifier <MaybeExtend> <MaybeImplement> <FieldList>

<ClassList> ::=
    | <Class> <ClassList>

MaybeExtend ::= 
    | : <CamelIdentifier> <NamespacePath>

MaybeImplement ::=
    | < CamelIdentifier <NamespacePath> <ImplementList>

ImplementList ::=
    | CamelIdentifier <NamespacePath> <ImplementList>

FieldList ::=
    | <MaybeStatic> <MaybeConstant> <Access> <FieldDefinition> <FieldList>

MaybeStatic ::=
    | static

MaybeConstant ::=
    | const

Access ::= +
    | #
    | -

FieldDefinition ::= SnakeIdentifier : <TypeNullable> <MaybeGet> <MaybeSet>
    | CamelIdentifier : <TypeList> => <TypeNullable> .

MaybeGet ::=
    | get

MaybeSet ::=
    | set

TypeNullable ::= <TypeSimple>
    | ? <TypeSimple>

<TypeSimple> ::= void
    | bool
    | int
    | double
    | string
    | array
    | callable
    | self
    | CamelIdentifier <NamespacePath>

<TypeList> ::=
    | <TypeNullable> <TypeList>
```

where `CamelIdentifier` is any string starting with an uppercase letter, and `SnakeIdentifier` is any string starting with a lowercase letter. Words in a `CamelCase` token are split on case change, while for `SnakeIdentifier` they are split on `_` characters.

## Examples

The following example will generate the layout for part of https://github.com/AuraSidera/Sextant/tree/v3.1:

```
base AuraSidera/Sextant

class State < ArrayAccess
    -url: string get
    -method: string get
    -parameters: array get
    -headers: array get
    -matches: array get
    -data: array get
    static +FromServer: Server => State
    static +FromDefault: => State
    +AddMatch: string void => self
   
class Server
    static +FromDefault: => self
    +GetUrl: => string
    +GetMethod: => string
    +GetParameters: => array
    +GetHeaders: => array

class Router
    -routes: array
    -default_action: callable
    static +FromDefaultAction: callable => self
    +Add: callable callable => self
    +Match: State => self

class SmartRouter : Router
    -condition_factory: callable
    -action_factory: callable
    static +FromFactories: callable callable callable => self
    +Add: callable callable => self
    -GetCondition: callable => callable
    -GetAction: callable => callable

namespace ConditionFactory
interface ConditionFactoryInterface
    +Invoke: => callable

class Always < ConditionFactoryInterface
    +Invoke: => callable

class Simple < ConditionFactoryInterface
    -url_pattern: UrlPattern
    -method: Method
    static +FromDefault: => self
    +Invoke: string string => callable

class UrlPattern < ConditionFactoryInterface
    static +TypePattern: string => string
    +Invoke: string => callable

class Method  < ConditionFactoryInterface
    +Invoke: string => callable

namespace ActionFactory
interface ActionFactoryInterface
    +Invoke: => callable

class Controller < ActionFactoryInterface
    -name_space: string
    +Invoke: string string => callable
```

